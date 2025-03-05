package com.vishalag53.offlinemusic.offline.others

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vishalag53.offlinemusic.R
import com.vishalag53.offlinemusic.databinding.ActivityOfflineMusicBinding
import com.vishalag53.offlinemusic.offline.player.Player

@Suppress("DEPRECATION")
class OfflineMusic : AppCompatActivity() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var offlineMusicBinding: ActivityOfflineMusicBinding
    }

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        offlineMusicBinding = ActivityOfflineMusicBinding.inflate(layoutInflater)
        setContentView(offlineMusicBinding.root)
        tabLayout = offlineMusicBinding.tabLayout
        viewPager = offlineMusicBinding.viewPager
        tabLayout()

        offlineMusicBinding.musicDisplayFolderName.isSelected = true
        offlineMusicBinding.musicProgressBar.visibility = View.GONE
        offlineMusicBinding.playMusic.visibility = View.GONE

        offlineMusicBinding.musicPlayPauseBTN.setOnClickListener {
            if (Player.isPlaying) pauseMusic()
            else playMusic()
        }

        offlineMusicBinding.musicPlayPrevious.setOnClickListener { prevNextBtn(false) }

        offlineMusicBinding.musicPlayNext.setOnClickListener { prevNextBtn(true) }

        offlineMusicBinding.playMusic.setOnClickListener {
            sendIntent("OfflineMusicNowPlaying", Player.songPosition, this)
//            val intent = Intent(this, Player::class.java)
//            intent.putExtra("index", Player.songPosition)
//            intent.putExtra("class", "OfflineMusicNowPlaying")
//            ContextCompat.startActivity(this, intent, null)
        }
    }

    override fun onResume() {
        super.onResume()
        if (Player.songService != null) {
            offlineMusicBinding.musicProgressBar.visibility = View.VISIBLE
            offlineMusicBinding.playMusic.visibility = View.VISIBLE
            offlineMusicBinding.musicDisplayFolderName.text = baseContext.getString(R.string.two_string_text, Player.songListPA[Player.songPosition].displayName, Player.songListPA[Player.songPosition].folderName)
            offlineMusicBinding.musicPosition.text = baseContext.getString(R.string.song_position, Player.songPosition + 1, Player.songListPA.size)

            if (Player.isPlaying) {
                offlineMusicBinding.musicPlayPauseBTN.setImageResource(R.drawable.pause_circle_outline_30dp)
            } else {
                offlineMusicBinding.musicPlayPauseBTN.setImageResource(R.drawable.play_circle_outline_30)
            }

            offlineMusicBinding.musicProgressBar.max = Player.songListPA[Player.songPosition].duration.toInt()
        }
    }

    private fun playMusic() {
        Player.songService!!.mediaPlayer!!.start()
        setPauseIcon()
        Player.isPlaying = true
    }

    private fun pauseMusic() {
        Player.songService!!.mediaPlayer!!.pause()
        setPlayIcon()
        Player.isPlaying = false
    }

    private fun prevNextBtn(increment: Boolean) {
        setSongPosition(increment)
        Player.songService!!.createMediaPlayer()
        offlineMusicBinding.musicDisplayFolderName.text = baseContext.getString(R.string.two_string_text, Player.songListPA[Player.songPosition].displayName, Player.songListPA[Player.songPosition].folderName)
        offlineMusicBinding.musicPosition.text = baseContext.getString(R.string.song_position, Player.songPosition + 1, Player.songListPA.size)
        Player.songService!!.showNotification(R.drawable.pause_circle_icon_48dp)
        playMusic()
    }

    //  TabLayout
    private fun tabLayout() {
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Songs"
                1 -> "Folders"
                2 -> "Playlists"
                else -> null
            }
        }.attach()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        if (!Player.isPlaying && Player.songService != null) {
//            Player.songService!!.stopForeground(true)
//            Player.songService!!.mediaPlayer!!.release()
//            Player.songService = null
//            exitProcess(1)
//        }
//    }
}