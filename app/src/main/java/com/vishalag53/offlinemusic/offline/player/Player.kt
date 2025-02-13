package com.vishalag53.offlinemusic.offline.player

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vishalag53.offlinemusic.R
import com.vishalag53.offlinemusic.databinding.ActivityPlayerBinding

class Player : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.musicPlay.setOnClickListener {
            it.visibility = View.INVISIBLE
            binding.musicPause.visibility = View.VISIBLE
        }

        binding.musicPause.setOnClickListener {
            it.visibility = View.INVISIBLE
            binding.musicPlay.visibility = View.VISIBLE
        }
    }
}