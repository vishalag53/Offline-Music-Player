package com.vishalag53.offlinemusic.offline.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vishalag53.offlinemusic.R
import com.vishalag53.offlinemusic.offline.data.setSongPosition
import com.vishalag53.offlinemusic.offline.player.Player.Companion.isPlaying
import com.vishalag53.offlinemusic.offline.player.Player.Companion.playerBinding
import com.vishalag53.offlinemusic.offline.player.Player.Companion.songListPA
import com.vishalag53.offlinemusic.offline.player.Player.Companion.songPosition
import com.vishalag53.offlinemusic.offline.player.Player.Companion.songService
import kotlin.system.exitProcess

@Suppress("DEPRECATION")
class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ApplicationClass.PREVIOUS -> prevNextSong(false, context!!)
            ApplicationClass.PLAY -> if (isPlaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> prevNextSong(true, context!!)
            ApplicationClass.EXIT -> {
                songService!!.stopForeground(true)
                songService!!.mediaPlayer!!.release()
                songService = null
                exitProcess(1)
            }
        }
    }

    private fun playMusic() {
        isPlaying = true
        songService!!.mediaPlayer!!.start()
        songService!!.showNotification(R.drawable.pause_circle_icon)
        playerBinding.musicPlayPause.setImageResource(R.drawable.pause_circle_icon)
    }

    private fun pauseMusic() {
        isPlaying = false
        songService!!.mediaPlayer!!.pause()
        songService!!.showNotification(R.drawable.play_circle_icon)
        playerBinding.musicPlayPause.setImageResource(R.drawable.play_circle_icon)
    }

    private fun prevNextSong(increment: Boolean, context: Context) {
        setSongPosition(increment)
        songService!!.createMediaPlayer()
        Glide.with(context)
            .load(songListPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_note_icon))
            .into(playerBinding.musicImage)
        playerBinding.musicTitle.text = songListPA[songPosition].title
        playMusic()
    }
}