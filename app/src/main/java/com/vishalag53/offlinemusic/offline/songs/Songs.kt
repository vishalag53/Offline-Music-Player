package com.vishalag53.offlinemusic.offline.songs

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishalag53.offlinemusic.databinding.FragmentSongsBinding
import com.vishalag53.offlinemusic.offline.data.LoadingSongData

class Songs : Fragment() {

    private lateinit var binding: FragmentSongsBinding
    private lateinit var songsAdapter: SongsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSongsBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val musicList = LoadingSongData.SongListMA
        binding.totalSongs.text = '(' + musicList.size.toString() + ')'

        binding.playRecycleView.setHasFixedSize(true)
        binding.playRecycleView.setItemViewCacheSize(10)
        binding.playRecycleView.layoutManager = LinearLayoutManager(this.context)
        songsAdapter = SongsAdapter(requireContext(), musicList)
        binding.playRecycleView.adapter = songsAdapter
    }
}