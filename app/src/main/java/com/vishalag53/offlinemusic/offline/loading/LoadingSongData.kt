package com.vishalag53.offlinemusic.offline.loading

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.vishalag53.offlinemusic.databinding.ActivityLoadingSongDataBinding
import com.vishalag53.offlinemusic.offline.others.OfflineMusic
import com.vishalag53.offlinemusic.offline.others.Song
import com.vishalag53.offlinemusic.offline.songs.Songs
import java.io.File

class LoadingSongData : AppCompatActivity() {

    private lateinit var binding: ActivityLoadingSongDataBinding

    companion object {
        lateinit var songListMA: ArrayList<Song>
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingSongDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestRuntimePermission()
        Songs.searchSF = false
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            songListMA = getAllAudio()
            val intent = Intent(this, OfflineMusic::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this,"Storage Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestRuntimePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_AUDIO
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        permissionLauncher.launch(permission)
    }

    //    Get All audi files
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("Recycle", "Range")
    private fun getAllAudio(): ArrayList<Song> {
        val tempList = ArrayList<Song>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media.MIME_TYPE + " LIKE 'audio/%'"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.BITRATE
        )
        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC", null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)) ?: "Unknown"
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)) ?: "Unknown"
                    val displayNameC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)) ?: "Unknown"
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)) ?: "Unknown"
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)) ?: "Unknown"
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val dateAddedC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED))
                    val dateModifiedC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val bitrateC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.BITRATE))
                    val sizeC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))
                    val albumIdc = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdc).toString()
                    val pathCC: List<String> =  pathC.split("/")
                    val folderName = pathCC[pathCC.size-2]

                    if (durationC > 0 && File(pathC).exists()) {
                        val song = Song(
                            id = idC,
                            displayName = displayNameC,
                            title = titleC,
                            album = albumC,
                            dateAdded = dateAddedC,
                            dateModified = dateModifiedC,
                            artist = artistC,
                            path = pathC,
                            duration = durationC,
                            artUri = artUriC,
                            folderName = folderName,
                            size = sizeC,
                            bitrate = bitrateC
                        )

                        tempList.add(song)
                    }

                } while (cursor.moveToNext())
                cursor.close()
            }
        }
        return tempList
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        if (!Player.isPlaying && Player.songService != null) {
//            Player.songService!!.stopForeground(true)
//            Player.songService!!.mediaPlayer!!.release()
//            Player.songService = null
//            exitProcess(1)
//        }
//    }
}