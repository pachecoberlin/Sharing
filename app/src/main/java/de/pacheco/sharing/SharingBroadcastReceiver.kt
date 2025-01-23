package de.pacheco.sharing

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log


class SharingBroadcastReceiver : BroadcastReceiver() {
    val TAG: String = "PachecoSharingAppBroadcastReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "Received intent with action: ${intent?.action}")

        if (intent != null && "de.pacheco.PLAY_SONG" == intent.action) {
            val songName = intent.getStringExtra("song_name")
            Log.d(TAG, "Received request to play song: $songName")
            playSong(context)
        }
    }

    private fun playSong(context: Context?) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
// Das hier spielt nicht automatisch ab -.-                       Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH).apply {
//            putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Media.ENTRY_CONTENT_TYPE)
//            putExtra(MediaStore.EXTRA_MEDIA_ARTIST, "Calle 13")
//            putExtra(MediaStore.EXTRA_MEDIA_TITLE, "Atrévete-te-te")
//            putExtra(SearchManager.QUERY, "Calle 13 Atrévete-te-te")
            component = ComponentName("com.spotify.music", "com.spotify.music.MainActivity")
            setData(Uri.parse("spotify:track:0DJDDTRaX2uQVPJBRjcjsF"));
        }
        context?.packageManager?.apply {
            if (intent.resolveActivity(this) != null) {
                context.startActivity(intent)
            }
// nur für system apps           if (this.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
//                startActivityAsUser(intent, userHandle);
//            }

        }
    }
}