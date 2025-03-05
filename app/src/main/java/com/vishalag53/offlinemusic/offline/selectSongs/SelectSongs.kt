package com.vishalag53.offlinemusic.offline.selectSongs

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishalag53.offlinemusic.R
import com.vishalag53.offlinemusic.databinding.ActivitySelectSongsBinding
import com.vishalag53.offlinemusic.offline.loading.LoadingSongData
import com.vishalag53.offlinemusic.offline.others.Song

class SelectSongs : AppCompatActivity() {

    companion object {
        val songListSelectSongs: MutableLiveData<ArrayList<Song>> by lazy {
            MutableLiveData(ArrayList())
        }
    }

    private lateinit var binding: ActivitySelectSongsBinding
    private lateinit var selectSongsAdapter: SelectSongsAdapter
    private lateinit var songList: ArrayList<Song>
    private var isSelectedAll: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectSongsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectSongsBack.setOnClickListener {
            songList = ArrayList()
            finish()
        }
        initializeLayout()

        songListSelectSongs.observe(this) { updatedList ->
            if(updatedList.isEmpty()) {
                binding.selectSongsText.setText(R.string.select_songs)
                binding.addToPlaylist.setImageResource(R.drawable.playlist_add_icon_unselect)
                binding.addToPlaylistTV.setTextColor(Color.parseColor("#898686"))
                binding.playNext.setImageResource(R.drawable.play_circle_icon_24dp_unselect)
                binding.playNextTV.setTextColor(Color.parseColor("#898686"))
                binding.musicDelete.setImageResource(R.drawable.delete_icon_unselect)
                binding.musicDeleteTV.setTextColor(Color.parseColor("#898686"))
                binding.share.setImageResource(R.drawable.share_icon_unselect)
                binding.shareTV.setTextColor(Color.parseColor("#898686"))
            } else {
                binding.selectSongsText.text = baseContext.getString(R.string.selected_songs, updatedList.size)
                binding.addToPlaylist.setImageResource(R.drawable.playlist_add_icon)
                binding.addToPlaylistTV.setTextColor(Color.parseColor("#FFFFFF"))
                binding.playNext.setImageResource(R.drawable.play_circle_icon_24dp)
                binding.playNextTV.setTextColor(Color.parseColor("#FFFFFF"))
                binding.musicDelete.setImageResource(R.drawable.delete_icon)
                binding.musicDeleteTV.setTextColor(Color.parseColor("#FFFFFF"))
                binding.share.setImageResource(R.drawable.share_icon)
                binding.shareTV.setTextColor(Color.parseColor("#FFFFFF"))
            }
        }

        binding.selectAllSongs.setOnClickListener { toggleSelectedAll() }
        binding.searchText.setOnClickListener { search() }
    }

    private fun initializeLayout() {
        when (intent.getStringExtra("class")) {
            "SongsFragmentSelectMultiple" -> {
                songList = ArrayList()
                songList.addAll(LoadingSongData.songListMA)
                setRecycleViewLayout()
            }
        }
    }

    private fun setRecycleViewLayout() {
        binding.selectRecycleView.setHasFixedSize(true)
        binding.selectRecycleView.setItemViewCacheSize(10)
        binding.selectRecycleView.layoutManager = LinearLayoutManager(this)
        selectSongsAdapter = SelectSongsAdapter(this,this , songList)
        binding.selectRecycleView.adapter = selectSongsAdapter
    }

    private fun search() {
        binding.removeSearchText.setOnClickListener {
            binding.searchText.text = null
            binding.removeSearchText.visibility = View.GONE
        }

        binding.searchText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                val updatedList = ArrayList<Song>()
                if (text != null) {
                    binding.removeSearchText.visibility = View.VISIBLE
                    val userInput = text.toString().lowercase()
                    for (song in songList) {
                        if (song.title.lowercase().contains(userInput) || song.displayName.lowercase().contains(userInput)) {
                            updatedList.add(song)
                        }
                        selectSongsAdapter.updateMusicList(updatedList)
                    }
                } else {
                    binding.removeSearchText.visibility = View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) = Unit
        })
    }

    private fun toggleSelectedAll() {
        val updatedList = ArrayList<Song>()

        if (isSelectedAll) {
            isSelectedAll = false
            binding.selectAllSongs.setImageResource(R.drawable.select_all_24dp)
        } else {
            updatedList.addAll(songList)
            isSelectedAll = true
            binding.selectAllSongs.setImageResource(R.drawable.deselect_all_24dp)
        }

        songListSelectSongs.value = updatedList
    }
}