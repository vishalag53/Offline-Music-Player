package com.vishalag53.offlinemusic.offline.player

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.vishalag53.offlinemusic.R
import com.vishalag53.offlinemusic.databinding.ActivityPlayerBinding
import com.vishalag53.offlinemusic.offline.loading.LoadingSongData
import com.vishalag53.offlinemusic.offline.others.Song
import com.vishalag53.offlinemusic.offline.others.exitApplication
import com.vishalag53.offlinemusic.offline.others.formatDuration
import com.vishalag53.offlinemusic.offline.others.formatSize
import com.vishalag53.offlinemusic.offline.others.setSongPosition
import com.vishalag53.offlinemusic.offline.others.setPauseIcon
import com.vishalag53.offlinemusic.offline.others.setPlayIcon
import com.vishalag53.offlinemusic.offline.service.SongService
import com.vishalag53.offlinemusic.offline.songInfo.SongInfo
import com.vishalag53.offlinemusic.offline.songs.Songs
import java.io.File
import kotlin.random.Random

class Player : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        lateinit var songListPA: ArrayList<Song>
        var songPosition: Int = 0
        var isPlaying: Boolean = false
        var songService: SongService? = null
        var sleepTimer: String? = null
        var playback_speed: Float = 1.0F
        @SuppressLint("StaticFieldLeak")
        lateinit var playerBinding: ActivityPlayerBinding
        var isRepeat: Boolean = false
        var nowPlayingId: String = ""
        var isShuffle: Boolean = false
    }

    private lateinit var dialogPlayerMenu: BottomSheetDialog
    private lateinit var popupViewSleepTimer: View
    private lateinit var dialogPlaybackSpeed: BottomSheetDialog
    private lateinit var playbackSpeedText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerBinding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(playerBinding.root)
        initializeLayout()

        playerBinding.moreMenu.setOnClickListener {
             showMenuBottomSheetDialog()
        }

        playerBinding.musicPlayPause.setOnClickListener {
            if(isPlaying) pauseMusic()
            else playMusic()
        }
        playerBinding.playMusicPrevious.setOnClickListener { previousNextSong(false) }
        playerBinding.playMusicNext.setOnClickListener { previousNextSong(true) }

        playerBinding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) songService!!.mediaPlayer!!.seekTo(progress)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) = Unit
            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        })

        playerBinding.musicPlaybackSpeed.setOnClickListener {
            openPlayBackSpeed()
        }

        playerBinding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun initializeLayout() {
        songPosition = intent.getIntExtra("index", 0)
        when(intent.getStringExtra("class")) {
            "SongAdapter" -> {
                startService()
                songListPA = ArrayList()
                songListPA.addAll(LoadingSongData.songListMA)
                playerBinding.musicPlaybackSpeed.text = this.getString(R.string.playback_speed_text_player, playback_speed.toString())
                setLayout()
            }
            "SongAdapterSearch" -> {
                startService()
                songListPA = ArrayList()
                songListPA.addAll(Songs.songListSearchSF)
                playerBinding.musicPlaybackSpeed.text = this.getString(R.string.playback_speed_text_player, playback_speed.toString())
                setLayout()
            }
            "SongsFragmentShuffle" -> {
                startService()
                songListPA = ArrayList()
                songListPA.addAll(LoadingSongData.songListMA)
                songPosition = Random.nextInt(songListPA.size)
                playerBinding.musicPlaybackSpeed.text = this.getString(R.string.playback_speed_text_player, playback_speed.toString())
                setLayout()
            }
            "OfflineMusicNowPlaying" -> {
                setLayout()
                playerBinding.musicStartMinutes.text = formatDuration(songService!!.mediaPlayer!!.currentPosition.toLong())
                playerBinding.musicEndMinutes.text = formatDuration(songService!!.mediaPlayer!!.duration.toLong())
                playerBinding.seekBar.progress = songService!!.mediaPlayer!!.currentPosition
                playerBinding.seekBar.max = songService!!.mediaPlayer!!.duration
                playerBinding.musicPlaybackSpeed.text = this.getString(R.string.playback_speed_text_player, playback_speed.toString())
                if (isPlaying) playerBinding.musicPlayPause.setImageResource(R.drawable.pause_circle_icon_48dp)
                else playerBinding.musicPlayPause.setImageResource(R.drawable.play_circle_icon_48dp)
            }
        }
    }

    private fun setLayout() {
        playerBinding.musicDisplayFolderName.text = baseContext.getString(R.string.two_string_text, songListPA[songPosition].displayName, songListPA[songPosition].folderName)
        playerBinding.musicDisplayFolderName.isSelected = true
    }

    private fun createMediaPlayer() {
        try {
            if (songService!!.mediaPlayer == null) songService!!.mediaPlayer = MediaPlayer()
            songService!!.mediaPlayer!!.reset()
            songService!!.mediaPlayer!!.setDataSource(songListPA[songPosition].path)
            songService!!.mediaPlayer!!.prepare()
            songService!!.mediaPlayer!!.start()
            isPlaying = true
            playerBinding.musicPlayPause.setImageResource(R.drawable.pause_circle_icon_48dp)
            songService!!.showNotification(R.drawable.pause_circle_icon_48dp)
            playerBinding.musicStartMinutes.text = formatDuration(songService!!.mediaPlayer!!.currentPosition.toLong())
            playerBinding.musicEndMinutes.text = formatDuration(songService!!.mediaPlayer!!.duration.toLong())
            playerBinding.seekBar.progress = 0
            playerBinding.seekBar.max = songService!!.mediaPlayer!!.duration
            songService!!.mediaPlayer!!.setOnCompletionListener(this)
            nowPlayingId = songListPA[songPosition].id
            songService!!.mediaPlayer!!.playbackParams = songService!!.mediaPlayer!!.playbackParams.setSpeed(playback_speed)
        }catch (e: Exception){
            return
        }
    }

    private fun startService() {
        val intentService = Intent(this, SongService::class.java)
        bindService(intentService, this, BIND_AUTO_CREATE)
        ContextCompat.startForegroundService(this, intentService)
    }

    private fun playMusic() {
        setPauseIcon()
        isPlaying = true
        songService!!.mediaPlayer!!.start()
    }

    private fun pauseMusic() {
        setPlayIcon()
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
        songService!!.progressBarSetup()
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

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 || resultCode == RESULT_OK) {
            return
        }
    }

    // Playback Speed
    private fun openPlayBackSpeed() {
        dialogPlaybackSpeed = BottomSheetDialog(this@Player)
        dialogPlaybackSpeed.setContentView(R.layout.playback_speed)
        dialogPlaybackSpeed.show()

        playbackSpeedText = dialogPlaybackSpeed.findViewById(R.id.playbackSpeed)!!
        playbackSpeedText.text = baseContext.getString(R.string.playback_speed_text, playerBinding.musicPlaybackSpeed.text)
        dialogPlaybackSpeed.findViewById<TextView>(R.id.playbackSpeed0_25)?.setOnClickListener { setPlaybackSpeed(0.25F) }
        dialogPlaybackSpeed.findViewById<TextView>(R.id.playbackSpeed0_5)?.setOnClickListener { setPlaybackSpeed(0.5F) }
        dialogPlaybackSpeed.findViewById<TextView>(R.id.playbackSpeed0_75)?.setOnClickListener { setPlaybackSpeed(0.75F) }
        dialogPlaybackSpeed.findViewById<TextView>(R.id.playbackSpeed1)?.setOnClickListener { setPlaybackSpeed(1.0F) }
        dialogPlaybackSpeed.findViewById<TextView>(R.id.playbackSpeed1_25)?.setOnClickListener { setPlaybackSpeed(1.25F) }
        dialogPlaybackSpeed.findViewById<TextView>(R.id.playbackSpeed1_5)?.setOnClickListener { setPlaybackSpeed(1.5F) }
        dialogPlaybackSpeed.findViewById<TextView>(R.id.playbackSpeed1_75)?.setOnClickListener { setPlaybackSpeed(1.75F) }
        dialogPlaybackSpeed.findViewById<TextView>(R.id.playbackSpeed2)?.setOnClickListener { setPlaybackSpeed(2.0F) }
        dialogPlaybackSpeed.findViewById<TextView>(R.id.playbackSpeed2_25)?.setOnClickListener { setPlaybackSpeed(2.25F) }
        dialogPlaybackSpeed.findViewById<TextView>(R.id.playbackSpeed2_5)?.setOnClickListener { setPlaybackSpeed(2.5F) }
        dialogPlaybackSpeed.findViewById<TextView>(R.id.playbackSpeed2_75)?.setOnClickListener { setPlaybackSpeed(2.75F) }
        dialogPlaybackSpeed.findViewById<TextView>(R.id.playbackSpeed3)?.setOnClickListener { setPlaybackSpeed(3.0F) }
        dialogPlaybackSpeed.findViewById<TextView>(R.id.playbackSpeed3_25)?.setOnClickListener { setPlaybackSpeed(3.25F) }
        dialogPlaybackSpeed.findViewById<TextView>(R.id.playbackSpeed3_5)?.setOnClickListener { setPlaybackSpeed(3.5F) }
        dialogPlaybackSpeed.findViewById<TextView>(R.id.playbackSpeed3_75)?.setOnClickListener { setPlaybackSpeed(3.75F) }
        dialogPlaybackSpeed.findViewById<TextView>(R.id.playbackSpeed4)?.setOnClickListener { setPlaybackSpeed(4.0F) }
    }

    private fun setPlaybackSpeed(playbackSpeed: Float) {
        playback_speed = playbackSpeed
        playbackSpeedText.text = this.getString(R.string.playback_speed_number, playback_speed)
        playerBinding.musicPlaybackSpeed.text = this.getString(R.string.playback_speed_text_player, playback_speed.toString())
        songService!!.mediaPlayer!!.playbackParams = songService!!.mediaPlayer!!.playbackParams.setSpeed(playbackSpeed)
        if (!isPlaying) {
            songService!!.mediaPlayer!!.pause()
        }
        dialogPlaybackSpeed.dismiss()
    }

    // menu
    private fun showMenuBottomSheetDialog() {
        dialogPlayerMenu = BottomSheetDialog(this)
        dialogPlayerMenu.setContentView(R.layout.player_menu_bottom_sheet_dialog)
        dialogPlayerMenu.show()

        if (isShuffle) dialogPlayerMenu.findViewById<ImageButton>(R.id.musicShuffle)?.setColorFilter(ContextCompat.getColor(this, R.color.bkg_color))
        if (isRepeat) dialogPlayerMenu.findViewById<ImageButton>(R.id.musicRepeat)?.setColorFilter(
            ContextCompat.getColor(this, R.color.bkg_color)
        )
        dialogPlayerMenu.findViewById<TextView>(R.id.displayName)?.text =
            songListPA[songPosition].displayName
        dialogPlayerMenu.findViewById<TextView>(R.id.totalTime)?.text =
            formatDuration(songListPA[songPosition].duration)
        dialogPlayerMenu.findViewById<TextView>(R.id.musicSize)?.text =
            formatSize(songListPA[songPosition].size)

        // About
        dialogPlayerMenu.findViewById<ImageButton>(R.id.musicInfo)?.setOnClickListener {
            dialogPlayerMenu.dismiss()
            val songInfoIntent = Intent(this, SongInfo::class.java)
            songInfoIntent.putExtra("position", songPosition)
            startActivity(songInfoIntent)
        }

        // Favourite
        dialogPlayerMenu.findViewById<ImageButton>(R.id.musicFavorite)?.setOnClickListener { }
        dialogPlayerMenu.findViewById<TextView>(R.id.musicFavoriteTV)?.setOnClickListener { }

        // Volume
        dialogPlayerMenu.findViewById<ImageButton>(R.id.musicVolume)?.setOnClickListener { }
        dialogPlayerMenu.findViewById<TextView>(R.id.musicVolumeTV)?.setOnClickListener { }

        // Share
        dialogPlayerMenu.findViewById<ImageButton>(R.id.musicShare)?.setOnClickListener { share() }
        dialogPlayerMenu.findViewById<TextView>(R.id.musicShareTV)?.setOnClickListener { share() }

        // Delete
        dialogPlayerMenu.findViewById<ImageButton>(R.id.musicDelete)?.setOnClickListener { }
        dialogPlayerMenu.findViewById<TextView>(R.id.musicDeleteTV)?.setOnClickListener { }

        // Sleep Timer
        dialogPlayerMenu.findViewById<ImageButton>(R.id.musicSleepTime)
            ?.setOnClickListener { sleepTimer() }
        dialogPlayerMenu.findViewById<TextView>(R.id.musicSleepTimeTV)
            ?.setOnClickListener { sleepTimer() }

        // Repeat
        dialogPlayerMenu.findViewById<ImageButton>(R.id.musicRepeat)
            ?.setOnClickListener { repeat() }
        dialogPlayerMenu.findViewById<TextView>(R.id.musicRepeatTV)?.setOnClickListener { repeat() }

        // Shuffle
        dialogPlayerMenu.findViewById<ImageButton>(R.id.musicShuffle)
            ?.setOnClickListener { shuffle() }
        dialogPlayerMenu.findViewById<TextView>(R.id.musicShuffleTV)
            ?.setOnClickListener { shuffle() }

        // Equalizer
        dialogPlayerMenu.findViewById<ImageButton>(R.id.musicEqualizer)
            ?.setOnClickListener { equalizer() }
        dialogPlayerMenu.findViewById<TextView>(R.id.musicEqualizerTV)
            ?.setOnClickListener { equalizer() }

    }

    private fun shuffle() {
        isShuffle = !isShuffle
        if (isShuffle) {
            dialogPlayerMenu.findViewById<ImageButton>(R.id.musicShuffle)
                ?.setColorFilter(ContextCompat.getColor(this, R.color.click_icon_color))
            Toast.makeText(this, "Shuffle is On", Toast.LENGTH_SHORT).show()
        } else {
            dialogPlayerMenu.findViewById<ImageButton>(R.id.musicShuffle)
                ?.setColorFilter(ContextCompat.getColor(this, R.color.white))
            Toast.makeText(this, "Shuffle is Off", Toast.LENGTH_SHORT).show()
        }
    }

    private fun repeat() {
        isRepeat = !isRepeat
        if (isRepeat) {
            dialogPlayerMenu.findViewById<ImageButton>(R.id.musicRepeat)
                ?.setColorFilter(ContextCompat.getColor(this, R.color.click_icon_color))
            Toast.makeText(this, "Repeat is On", Toast.LENGTH_SHORT).show()
        } else {
            dialogPlayerMenu.findViewById<ImageButton>(R.id.musicRepeat)
                ?.setColorFilter(ContextCompat.getColor(this, R.color.white))
            Toast.makeText(this, "Repeat is Off", Toast.LENGTH_SHORT).show()
        }
    }

    private fun equalizer() {
        try {
            val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
            eqIntent.putExtra(
                AudioEffect.EXTRA_AUDIO_SESSION,
                songService!!.mediaPlayer!!.audioSessionId
            )
            eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
            eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
            startActivityForResult(eqIntent, 100)
        } catch (e: Exception) {
            Toast.makeText(this, "Equalizer Feature not Supported!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun share() {
        val audioFile = File(songListPA[songPosition].path)
        val uri = FileProvider.getUriForFile(this, "${packageName}.provider", audioFile)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "audio/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share Music"))
    }

    private fun sleepTimer() {
        dialogPlayerMenu.dismiss()
        val inflater = LayoutInflater.from(this)
        popupViewSleepTimer = inflater.inflate(R.layout.sleep_timer_alert_dialog, null)
        val builderSleepTimer = AlertDialog.Builder(this)
        builderSleepTimer.setView(popupViewSleepTimer)
        val dialogSleepTimer = builderSleepTimer.create()
        dialogSleepTimer.show()

        val radioGroup = popupViewSleepTimer.findViewById<RadioGroup>(R.id.sleepTimerRadioGroup)
        radioGroup.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            val selectedRadioButton =
                popupViewSleepTimer.findViewById<RadioButton>(selectedRadioButtonId)
            Player.sleepTimer = selectedRadioButton.text.toString()
            dialogSleepTimer.dismiss()
        }

        val custom = popupViewSleepTimer.findViewById<TextView>(R.id.sleepTimerCustom)
        custom.setOnClickListener {
            dialogSleepTimer.dismiss()
            val popupViewSleepTimerCustom: View =
                inflater.inflate(R.layout.custom_sleep_timer_alert_dialog, null)
            val builderSleepTimerCustom = AlertDialog.Builder(this)
            builderSleepTimerCustom.setView(popupViewSleepTimerCustom)
            val dialogSleepTimerCustom = builderSleepTimerCustom.create()
            dialogSleepTimerCustom.show()

//            var customSleepTimer: String
//            val customSleepTimerEditText = popupViewSleepTimerCustom.findViewById<EditText>(R.id.customSleepTimeEditText)
        }

    }

    private fun startSleepTimer(time: Long) {
        dialogPlayerMenu.findViewById<ImageButton>(R.id.musicSleepTime)
            ?.setColorFilter(R.color.click_icon_color)
        popupViewSleepTimer.findViewById<TextView>(R.id.musicStopTimeText).text =
            baseContext.getString(R.string.stop_time_text, time)
        Thread {
            Thread.sleep(time * 60000)
            exitApplication()
        }.start()
    }
}