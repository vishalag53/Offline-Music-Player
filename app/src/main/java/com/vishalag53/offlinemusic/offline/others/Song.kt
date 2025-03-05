@file:Suppress("DEPRECATION")

package com.vishalag53.offlinemusic.offline.others

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.vishalag53.offlinemusic.R
import com.vishalag53.offlinemusic.offline.player.Player
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class Song(
    val id: String,
    val displayName: String,
    val title: String,
    val album: String,
    val dateAdded: Long,
    val dateModified: Long,
    val artist: String,
    val duration: Long = 0,
    val path: String,
    val artUri: String,
    val folderName: String,
    val size: Long,
    val bitrate: Long
)

@SuppressLint("DefaultLocale")
fun formatDuration(duration: Long): String {
    val hours = TimeUnit.HOURS.convert(duration, TimeUnit.MILLISECONDS)
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS) -
            hours * TimeUnit.MINUTES.convert(1, TimeUnit.HOURS)
    val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
            hours * TimeUnit.SECONDS.convert(1, TimeUnit.HOURS) -
            minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}


fun setSongPosition(increment: Boolean) {
    if (!Player.isRepeat) {
        if (increment) {
            if (Player.songPosition == Player.songListPA.size - 1) Player.songPosition = 0
            else ++Player.songPosition
        } else {
            if (Player.songPosition == 0) Player.songPosition = Player.songListPA.size - 1
            else --Player.songPosition
        }
    }
}

@SuppressLint("DefaultLocale")
fun formatSize(size: Long): String {
    val sizeMB = size / (1024.0 * 1024.0)
    return String.format("%.2f MB", sizeMB)
}

@SuppressLint("DefaultLocale")
fun formatBitrate(bitrate: Long): String {
    val bitrateKBPS = bitrate / 1000
    return String.format("%d kbps", bitrateKBPS)
}

fun formatDate(date: Long): String {
    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    return simpleDateFormat.format(Date(date * 1000L))
}

fun exitApplication() {
    if(Player.songService != null ){
        Player.songService!!.stopForeground(true)
        Player.songService!!.mediaPlayer!!.release()
        Player.songService = null
        exitProcess(1)
    }
}

fun setPauseIcon() {
    OfflineMusic.offlineMusicBinding.musicPlayPauseBTN.setImageResource(R.drawable.pause_circle_outline_30dp)
    Player.songService!!.showNotification(R.drawable.pause_circle_icon_48dp)
    Player.playerBinding.musicPlayPause.setImageResource(R.drawable.pause_circle_icon_48dp)
}

fun setPlayIcon() {
    OfflineMusic.offlineMusicBinding.musicPlayPauseBTN.setImageResource(R.drawable.play_circle_outline_30)
    Player.songService!!.showNotification(R.drawable.play_circle_icon_48dp)
    Player.playerBinding.musicPlayPause.setImageResource(R.drawable.play_circle_icon_48dp)
}

fun sendIntent(ref: String, position: Int, context: Context) {
    val intent = Intent(context, Player::class.java)
    intent.putExtra("index", position)
    intent.putExtra("class", ref)
    ContextCompat.startActivity(context, intent, null)
}

