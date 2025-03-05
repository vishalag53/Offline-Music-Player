package com.vishalag53.offlinemusic.offline.songs

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishalag53.offlinemusic.R
import com.vishalag53.offlinemusic.databinding.FragmentSongsBinding
import com.vishalag53.offlinemusic.offline.others.Song
import com.vishalag53.offlinemusic.offline.others.formatDuration
import com.vishalag53.offlinemusic.offline.loading.LoadingSongData
import com.vishalag53.offlinemusic.offline.selectSongs.SelectSongs
import com.vishalag53.offlinemusic.offline.player.Player

class Songs : Fragment() {

    companion object {
        lateinit var songListSearchSF: ArrayList<Song>
        var searchSF: Boolean = false
    }

    private lateinit var binding: FragmentSongsBinding
    private lateinit var songsAdapter: SongsAdapter
    private var flagSearchViewShow: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSongsBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val musicList = LoadingSongData.songListMA
        binding.totalSongs.text = context?.getString(R.string.total_songs_text, musicList.size)
        totalSongTime(musicList)

        binding.playRecycleView.setHasFixedSize(true)
        binding.playRecycleView.setItemViewCacheSize(10)
        binding.playRecycleView.layoutManager = LinearLayoutManager(this.context)
        songsAdapter = SongsAdapter(requireContext(), musicList)
        binding.playRecycleView.adapter = songsAdapter

        binding.musicSearch.setOnClickListener { search() }
        binding.musicShuffle.setOnClickListener {
            val intent = Intent(requireContext(), Player::class.java)
            intent.putExtra("class","SongsFragmentShuffle")
            startActivity(intent)
        }
        binding.musicSelectMultiple.setOnClickListener {
            val selectSongsIntent = Intent(requireContext(), SelectSongs::class.java)
            selectSongsIntent.putExtra("class","SongsFragmentSelectMultiple")
            startActivity(selectSongsIntent)
        }
    }

    private fun totalSongTime(musicList: ArrayList<Song>) {
        var totalTime: Long = 0
        for(song in musicList) {
            totalTime += song.duration
        }
        binding.totalMusicTime.text =  context?.getString(R.string.total_time_text, formatDuration(totalTime))
    }

    private fun search() {
        flagSearchViewShow = !flagSearchViewShow
        if (flagSearchViewShow) {
            binding.searchViewCl.visibility = View.VISIBLE

            binding.removeSearchText.setOnClickListener {
                binding.searchText.text = null
                binding.removeSearchText.visibility = View.GONE
            }

            binding.searchText.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

                override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                    songListSearchSF = ArrayList()
                    if (text != null) {
                        binding.removeSearchText.visibility = View.VISIBLE
                        val userInput = text.toString().lowercase()
                        for (song in LoadingSongData.songListMA) {
                            if (song.title.lowercase().contains(userInput) || song.displayName.lowercase().contains(userInput)) {
                                songListSearchSF.add(song)
                            }
                            searchSF = true
                            songsAdapter.updateMusicList(songListSearchSF)
                        }
                    } else {
                        searchSF = false
                        binding.removeSearchText.visibility = View.GONE
                    }
                }

                override fun afterTextChanged(p0: Editable?) = Unit

            })

        } else {
            binding.searchViewCl.visibility = View.GONE
            if (binding.searchText.text.isNotEmpty()) {
                searchSF = true
                binding.removeSearchText.visibility = View.VISIBLE
            } else {
                searchSF = false
                binding.removeSearchText.visibility = View.GONE
            }
        }
    }
}