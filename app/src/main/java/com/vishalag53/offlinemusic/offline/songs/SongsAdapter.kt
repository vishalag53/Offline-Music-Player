package com.vishalag53.offlinemusic.offline.songs

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishalag53.offlinemusic.databinding.PlayMusicBinding
import com.vishalag53.offlinemusic.offline.data.Song

class SongsAdapter(private val context: Context, private val songList: ArrayList<Song>): RecyclerView.Adapter<SongsAdapter.MyHolder>() {
    class MyHolder(binding: PlayMusicBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.songImage
        val name = binding.songName
        val durationFolder = binding.durationFolder
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsAdapter.MyHolder {
        return MyHolder(PlayMusicBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: SongsAdapter.MyHolder, position: Int) {
        holder.name.text = songList[position].title
        holder.durationFolder.text = songList[position].duration.toString()
    }

    override fun getItemCount(): Int {
        return songList.size
    }
}