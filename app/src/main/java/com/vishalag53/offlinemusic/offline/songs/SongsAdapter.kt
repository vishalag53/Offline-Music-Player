package com.vishalag53.offlinemusic.offline.songs

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vishalag53.offlinemusic.R
import com.vishalag53.offlinemusic.databinding.PlayMusicBinding
import com.vishalag53.offlinemusic.offline.data.Song
import com.vishalag53.offlinemusic.offline.data.formatDuration
import com.vishalag53.offlinemusic.offline.player.Player

@Suppress("DEPRECATION")
class SongsAdapter(private val context: Context, private val songList: ArrayList<Song>): RecyclerView.Adapter<SongsAdapter.MyHolder>() {
    class MyHolder(binding: PlayMusicBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.songImage
        val name = binding.songName
        val durationFolder = binding.durationFolder
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(PlayMusicBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = songList[position].displayName
        holder.durationFolder.text = formatDuration(songList[position].duration) + " - " + songList[position].folderName
        Glide.with(context)
            .load(songList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_note_icon))
            .into(holder.image)
        holder.root.setOnClickListener{
            val intent = Intent(context, Player::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "SongAdapter")
            ContextCompat.startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return songList.size
    }
}