package com.vishalag53.offlinemusic.offline.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.vishalag53.offlinemusic.R
import com.vishalag53.offlinemusic.offline.others.OfflineMusic
import com.vishalag53.offlinemusic.offline.others.formatDuration
import com.vishalag53.offlinemusic.offline.player.Player

class SongService: Service() {

    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnableSeekBar: Runnable
    private lateinit var runnableProgressBar: Runnable

    override fun onBind(p0: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification(R.drawable.pause_circle_icon_48dp)
        return START_STICKY
    }

    inner class MyBinder: Binder() {
        fun currentService(): SongService {
            return this@SongService
        }
    }

    @SuppressLint("ForegroundServiceType")
    fun showNotification(playPauseBtn: Int) {
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val prevIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, flag)

        val playIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, flag)

        val nextIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, flag)

        val exitIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, flag)

        val notification = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentTitle(Player.songListPA[Player.songPosition].displayName)
            .setContentText(Player.songListPA[Player.songPosition].folderName)
            .setSmallIcon(R.drawable.music_note)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.previous_icon_48, "Previous", prevPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.next_icon_48, "Next", nextPendingIntent)
            .addAction(R.drawable.exit_icon, "Exit", exitPendingIntent)
            .build()

        startForeground(100, notification)
    }

    fun createMediaPlayer() {
        try {
            if (Player.songService!!.mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(Player.songListPA[Player.songPosition].path)
            mediaPlayer!!.prepare()
            Player.playerBinding.musicPlayPause.setImageResource(R.drawable.pause_circle_icon_48dp)
            showNotification(R.drawable.pause_circle_icon_48dp)
            Player.playerBinding.musicStartMinutes.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            Player.playerBinding.musicEndMinutes.text = formatDuration(mediaPlayer!!.duration.toLong())
            Player.playerBinding.seekBar.progress = 0
            Player.playerBinding.seekBar.max = mediaPlayer!!.duration
            Player.nowPlayingId = Player.songListPA[Player.songPosition].id
        }catch (e: Exception){
            return
        }
    }

    fun seekBarSetup() {
        runnableSeekBar = Runnable {
            Player.playerBinding.musicStartMinutes.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            Player.playerBinding.seekBar.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnableSeekBar, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnableSeekBar, 0)
    }

    fun progressBarSetup() {
        runnableProgressBar = Runnable {
            OfflineMusic.offlineMusicBinding.musicProgressBar.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnableProgressBar, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnableProgressBar, 0)
    }
}