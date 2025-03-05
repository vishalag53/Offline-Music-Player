package com.vishalag53.offlinemusic.offline.songs

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishalag53.offlinemusic.R
import com.vishalag53.offlinemusic.databinding.PlayMusicBinding
import com.vishalag53.offlinemusic.offline.others.Song
import com.vishalag53.offlinemusic.offline.others.formatDuration
import com.vishalag53.offlinemusic.offline.others.sendIntent
import com.vishalag53.offlinemusic.offline.player.Player

class SongsAdapter(private val context: Context, private var songList: ArrayList<Song>): RecyclerView.Adapter<SongsAdapter.MyHolder>() {
    class MyHolder(binding: PlayMusicBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.songName
        val durationFolder = binding.durationFolder
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(PlayMusicBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = songList[position].displayName
        holder.durationFolder.text = context.getString(R.string.two_string_text, formatDuration(songList[position].duration), songList[position].folderName)
        holder.root.setOnClickListener{
            when {
                songList[position].id == Player.nowPlayingId -> sendIntent("OfflineMusicNowPlaying", position, context)
                Songs.searchSF -> sendIntent("SongAdapterSearch", position,context)
                else -> sendIntent("SongAdapter", position, context)
            }
        }
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicList(searchList: ArrayList<Song>) {
        songList = ArrayList()
        songList.addAll(searchList)
        notifyDataSetChanged()
    }

}