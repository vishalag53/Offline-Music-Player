package com.vishalag53.offlinemusic.offline.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vishalag53.offlinemusic.R
import com.vishalag53.offlinemusic.offline.others.OfflineMusic
import com.vishalag53.offlinemusic.offline.others.exitApplication
import com.vishalag53.offlinemusic.offline.others.setPauseIcon
import com.vishalag53.offlinemusic.offline.others.setPlayIcon
import com.vishalag53.offlinemusic.offline.others.setSongPosition
import com.vishalag53.offlinemusic.offline.player.Player

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ApplicationClass.PREVIOUS -> prevNextSong(false, context!!)
            ApplicationClass.PLAY -> if (Player.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> prevNextSong(true, context!!)
            ApplicationClass.EXIT -> exitApplication()
        }
    }

    private fun playMusic() {
        Player.isPlaying = true
        Player.songService!!.mediaPlayer!!.start()
        setPauseIcon()
    }

    private fun pauseMusic() {
        Player.isPlaying = false
        Player.songService!!.mediaPlayer!!.pause()
        setPlayIcon()
    }

    private fun prevNextSong(increment: Boolean, context: Context) {
        setSongPosition(increment)
        Player.songService!!.createMediaPlayer()
        Player.playerBinding.musicDisplayFolderName.text = context.getString(R.string.two_string_text, Player.songListPA[Player.songPosition].displayName, Player.songListPA[Player.songPosition].folderName)
        OfflineMusic.offlineMusicBinding.musicDisplayFolderName.text = context.getString(R.string.two_string_text, Player.songListPA[Player.songPosition].displayName, Player.songListPA[Player.songPosition].folderName)
        OfflineMusic.offlineMusicBinding.musicPosition.text = context.getString(R.string.song_position, Player.songPosition + 1, Player.songListPA.size)
        Player.songService!!.mediaPlayer!!.playbackParams = Player.songService!!.mediaPlayer!!.playbackParams.setSpeed(Player.playback_speed)
        playMusic()
    }
}