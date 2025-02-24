package com.vishalag53.offlinemusic.offline

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.vishalag53.offlinemusic.R
import com.vishalag53.offlinemusic.databinding.ActivityOfflineMusicBinding
import com.vishalag53.offlinemusic.offline.adapter.ViewPagerAdapter
import com.vishalag53.offlinemusic.offline.player.Player
import kotlin.system.exitProcess

@Suppress("DEPRECATION")
class OfflineMusic : AppCompatActivity() {

    private lateinit var binding: ActivityOfflineMusicBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_OfflineMusic)
        binding = ActivityOfflineMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        tabLayout()

        binding.playMusic.setOnClickListener {
            val intent = Intent(this, Player::class.java)
            startActivity(intent)
        }
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