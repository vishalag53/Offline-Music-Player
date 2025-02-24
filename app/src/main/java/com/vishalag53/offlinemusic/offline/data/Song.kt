package com.vishalag53.offlinemusic.offline.data

import android.annotation.SuppressLint
import com.vishalag53.offlinemusic.offline.player.Player
import com.vishalag53.offlinemusic.offline.player.Player.Companion.songListPA
import com.vishalag53.offlinemusic.offline.player.Player.Companion.songPosition
import java.util.concurrent.TimeUnit

data class Song(
    val id: String,
    val displayName: String,
    val title: String,
    val album: String,
    val artist: String,
    val duration: Long = 0,
    val path: String,
    val artUri: String,
    val folderName: String
)

@SuppressLint("DefaultLocale")
fun formatDuration(duration: Long): String {
    val hours = TimeUnit.HOURS.convert(duration, TimeUnit.MILLISECONDS)
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS) -
            hours * TimeUnit.MINUTES.convert(1, TimeUnit.HOURS)
    val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
            hours * TimeUnit.SECONDS.convert(1, TimeUnit.HOURS) -
            minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return if(hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

fun setSongPosition(increment: Boolean) {
    if(!Player.isRepeat) {
        if(increment) {
            if(songPosition == songListPA.size - 1) songPosition = 0
            else ++songPosition
        } else {
            if(songPosition == 0) songPosition = songListPA.size - 1
            else --songPosition
        }
    }
}