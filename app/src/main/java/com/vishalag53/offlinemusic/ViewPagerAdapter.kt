package com.vishalag53.offlinemusic

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.vishalag53.offlinemusic.folders.Folders
import com.vishalag53.offlinemusic.playlists.Playlists
import com.vishalag53.offlinemusic.songs.Songs

class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Songs()
            1 -> Folders()
            2 -> Playlists()
            else -> Songs()
        }
    }
}