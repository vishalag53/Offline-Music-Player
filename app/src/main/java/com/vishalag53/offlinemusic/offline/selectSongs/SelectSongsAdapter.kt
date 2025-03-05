package com.vishalag53.offlinemusic.offline.selectSongs

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.vishalag53.offlinemusic.R
import com.vishalag53.offlinemusic.databinding.SelectMusicBinding
import com.vishalag53.offlinemusic.offline.others.Song

class SelectSongsAdapter(private val context: Context, private val lifecycleOwner: LifecycleOwner , private var songList: ArrayList<Song>): RecyclerView.Adapter<SelectSongsAdapter.MyHolder>()  {

    class MyHolder(binding: SelectMusicBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.musicDisplayFolderName
        val checkBox = binding.checkbox
        val selectMusicCL = binding.selectMusicCL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(SelectMusicBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val song = songList[position]
        holder.name.text = context.getString(R.string.two_string_text, song.displayName, song.folderName)

        SelectSongs.songListSelectSongs.observe(lifecycleOwner){ updatedList ->
            if (updatedList.contains(song)) {
                holder.checkBox.setImageResource(R.drawable.check_box_24dp)
            } else {
                holder.checkBox.setImageResource(R.drawable.uncheck_box_24dp)
            }
        }

        holder.name.setOnClickListener { addRemoveSelectSong(song, holder) }
        holder.selectMusicCL.setOnClickListener { addRemoveSelectSong(song, holder) }
        holder.checkBox.setOnClickListener { addRemoveSelectSong(song, holder) }
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    private fun addRemoveSelectSong(song: Song, holder: MyHolder) {
        val updatedList = SelectSongs.songListSelectSongs.value ?: arrayListOf()

        if (updatedList.contains(song)) {
            updatedList.remove(song)
            holder.checkBox.setImageResource(R.drawable.uncheck_box_24dp)
        } else {
            updatedList.add(song)
            holder.checkBox.setImageResource(R.drawable.check_box_24dp)
        }

        SelectSongs.songListSelectSongs.value = updatedList
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicList(searchList: ArrayList<Song>) {
        songList = ArrayList()
        songList.addAll(searchList)
        notifyDataSetChanged()
    }
}