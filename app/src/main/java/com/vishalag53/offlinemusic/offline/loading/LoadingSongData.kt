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
import com.vishalag53.offlinemusic.R
import com.vishalag53.offlinemusic.databinding.ActivityLoadingSongDataBinding
import com.vishalag53.offlinemusic.offline.OfflineMusic
import com.vishalag53.offlinemusic.offline.data.Song
import com.vishalag53.offlinemusic.offline.player.Player
import java.io.File
import kotlin.system.exitProcess

@Suppress("DEPRECATION")
class LoadingSongData : AppCompatActivity() {

    private lateinit var binding: ActivityLoadingSongDataBinding

    companion object {
        lateinit var SongListMA: ArrayList<Song>
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_OfflineMusic)
        binding = ActivityLoadingSongDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestRuntimePermission()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            SongListMA = getAllAudio()
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
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DISPLAY_NAME
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
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
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
                            artist = artistC,
                            path = pathC,
                            duration = durationC,
                            artUri = artUriC,
                            folderName = folderName
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

// Title = String                       e.g.  "Shivam Ep 599"
// Date added = Long                    e.g.   "1740294010"
// Album Artist = String                e.g.  "null"
// Artist ID = Long                     e.g.   "3085506369950961584"
// Author = String                      e.g.   "null"
// Bitrate = Long                       e.g.   "64000"
// Bookmark = String                    e.g.   "null"
// Bucket Display name = String         e.g.  "Shivam The Hidden warrior"
// Bucket ID = Long                     e.g.    "-1774890527"
// Capture Framework = String           e.g.   "null"
// Display name = String                e.g.  "STHW 599.mp3"
// Data = String                        e.g.        "/storage/emulated/0/VISHAL Agrawal/Audio/Pocket FM/Shivam The Hidden warrior/STHW 599.mp3"
// Size = Long                          e.g. "5711696"