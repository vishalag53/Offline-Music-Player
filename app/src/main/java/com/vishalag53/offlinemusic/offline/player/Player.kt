package com.vishalag53.offlinemusic.offline.player

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vishalag53.offlinemusic.R
import com.vishalag53.offlinemusic.databinding.ActivityPlayerBinding
import com.vishalag53.offlinemusic.offline.OfflineMusic
import com.vishalag53.offlinemusic.offline.data.Song
import com.vishalag53.offlinemusic.offline.data.formatDuration
import com.vishalag53.offlinemusic.offline.data.setSongPosition
import com.vishalag53.offlinemusic.offline.loading.LoadingSongData
import com.vishalag53.offlinemusic.offline.service.SongService
import kotlin.random.Random

class Player : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        lateinit var songListPA: ArrayList<Song>
        var songPosition: Int = 0
        var isPlaying: Boolean = false
        var isShuffle: Boolean = false
        var songService: SongService? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var playerBinding: ActivityPlayerBinding
        var isRepeat: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerBinding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(playerBinding.root)

        // for Starting Service
        val intentService = Intent(this, SongService::class.java)
        bindService(intentService, this, BIND_AUTO_CREATE)
        ContextCompat.startForegroundService(this, intentService)

        initializeLayout()
        playerBinding.musicPlayPause.setOnClickListener {
            if(isPlaying) pauseMusic()
            else playMusic()
        }
        playerBinding.playMusicPrevious.setOnClickListener { previousNextSong(false) }
        playerBinding.playMusicNext.setOnClickListener { previousNextSong(true) }
        playerBinding.musicShuffle.setOnClickListener {
            isShuffle = !isShuffle
            if (isShuffle) {
                playerBinding.musicShuffle.setColorFilter(ContextCompat.getColor(this, R.color.black))
                Toast.makeText(this, "Shuffle is On", Toast.LENGTH_SHORT).show()
            } else {
                playerBinding.musicShuffle.setColorFilter(ContextCompat.getColor(this, R.color.white))
                Toast.makeText(this, "Shuffle is Off", Toast.LENGTH_SHORT).show()
            }
        }
        playerBinding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) songService!!.mediaPlayer!!.seekTo(progress)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) = Unit
            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        })
        playerBinding.musicRepeat.setOnClickListener {
            isRepeat = !isRepeat
            if (isRepeat) {
                playerBinding.musicRepeat.setColorFilter(ContextCompat.getColor(this, R.color.black))
                Toast.makeText(this, "Repeat is On", Toast.LENGTH_SHORT).show()
            } else {
                playerBinding.musicRepeat.setColorFilter(ContextCompat.getColor(this, R.color.white))
                Toast.makeText(this, "Repeat is Off", Toast.LENGTH_SHORT).show()
            }
        }
        playerBinding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun initializeLayout() {
        songPosition = intent.getIntExtra("index", 0)
        when(intent.getStringExtra("class")) {
            "SongAdapter" -> {
                songListPA = ArrayList()
                songListPA.addAll(LoadingSongData.SongListMA)
                setLayout()
            }
        }
    }

    private fun setLayout() {
        Glide.with(this)
            .load(songListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_note_icon))
            .into(playerBinding.musicImage)
        playerBinding.musicTitle.text = songListPA[songPosition].displayName
        if (isShuffle) playerBinding.musicShuffle.setColorFilter(ContextCompat.getColor(this, R.color.bkg_color))
        if (isRepeat) playerBinding.musicRepeat.setColorFilter(ContextCompat.getColor(this, R.color.bkg_color))
    }

    private fun createMediaPlayer() {
        try {
            if (songService!!.mediaPlayer == null) songService!!.mediaPlayer = MediaPlayer()
            songService!!.mediaPlayer!!.reset()
            songService!!.mediaPlayer!!.setDataSource(songListPA[songPosition].path)
            songService!!.mediaPlayer!!.prepare()
            songService!!.mediaPlayer!!.start()
            isPlaying = true
            playerBinding.musicPlayPause.setImageResource(R.drawable.pause_circle_icon)
            songService!!.showNotification(R.drawable.pause_circle_icon)
            playerBinding.musicStartMinutes.text = formatDuration(songService!!.mediaPlayer!!.currentPosition.toLong())
            playerBinding.musicEndMinutes.text = formatDuration(songService!!.mediaPlayer!!.duration.toLong())
            playerBinding.seekBar.progress = 0
            playerBinding.seekBar.max = songService!!.mediaPlayer!!.duration
            songService!!.mediaPlayer!!.setOnCompletionListener(this)
        }catch (e: Exception){
            return
        }
    }

    private fun playMusic() {
        playerBinding.musicPlayPause.setImageResource(R.drawable.pause_circle_icon)
        songService!!.showNotification(R.drawable.pause_circle_icon)
        isPlaying = true
        songService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        playerBinding.musicPlayPause.setImageResource(R.drawable.play_circle_icon)
        songService!!.showNotification(R.drawable.play_circle_icon)
        isPlaying = false
        songService!!.mediaPlayer!!.pause()
    }

    private fun previousNextSong(increment: Boolean) {
        if (increment) {
            setSongPosition(true)
            setLayout()
            createMediaPlayer()
        } else {
            setSongPosition(false)
            setLayout()
            createMediaPlayer()
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as SongService.MyBinder
        songService = binder.currentService()
        createMediaPlayer()
        songService!!.seekBarSetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        songService = null
    }

    override fun onCompletion(mediaPlayer: MediaPlayer?) {
        setSongPosition(true)
        if(isShuffle && !isRepeat) {
            songPosition = Random.nextInt(songListPA.size)
        }
        createMediaPlayer()
        try {
            setLayout()
        } catch (e:Exception) {
            return
        }
    }
}