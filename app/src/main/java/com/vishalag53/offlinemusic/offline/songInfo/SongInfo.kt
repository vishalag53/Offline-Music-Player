package com.vishalag53.offlinemusic.offline.songInfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vishalag53.offlinemusic.databinding.ActivitySongInfoBinding
import com.vishalag53.offlinemusic.offline.others.formatBitrate
import com.vishalag53.offlinemusic.offline.others.formatDate
import com.vishalag53.offlinemusic.offline.others.formatDuration
import com.vishalag53.offlinemusic.offline.others.formatSize
import com.vishalag53.offlinemusic.offline.player.Player

class SongInfo : AppCompatActivity() {

    private lateinit var binding: ActivitySongInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val position = intent.getIntExtra("position",0)
        val songDetail = Player.songListPA[position]

        binding.songInfoBack.setOnClickListener { finish() }

        binding.songTitle.text = songDetail.title
        binding.songDisplayName.text = songDetail.displayName
        binding.songArtist.text = songDetail.artist
        binding.songAlbum.text = songDetail.album
        binding.songDuration.text = formatDuration(songDetail.duration)
        binding.songSize.text = formatSize(songDetail.size)
        binding.songBitrate.text = formatBitrate(songDetail.bitrate)
        binding.songDateAdded.text = formatDate(songDetail.dateAdded)
        binding.songDateModified.text = formatDate(songDetail.dateModified)
        binding.songFolderName.text = songDetail.folderName
        binding.songFilePath.text = songDetail.path
    }
}