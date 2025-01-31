package de.pacheco.sharing

import android.app.AlertDialog
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.UserManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import de.pacheco.sharing.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    val SUPPORTS_MULTIPLE_USERS: Boolean = UserManager.supportsMultipleUsers()

//    private val mContext: Context = InstrumentationRegistry.getInstrumentation().context

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    val TAG: String = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.black);

        // fullscreen
        val controller = window.insetsController
        if (controller != null) {
            controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        Log.d(TAG, "intent: $intent")
        val linksharing = intent.getBooleanExtra("linksharing", false)
        val startDefault = true
        val testing = true
        Log.d(TAG, "linksharing: $linksharing; startDefault: $startDefault; testing: $testing")
        if (linksharing) {
            wannaFollowLinkDialog()
        } else {
            if (startDefault) {
                val serviceIntent = Intent(this, ForegroundService::class.java)
                startForegroundService(serviceIntent)
                startKitchenSink()
                finish()
            }
            if (testing) {
//            val serviceIntent = Intent(this, ForegroundService::class.java)
//            startForegroundService(serviceIntent)
//            CoroutineScope(Dispatchers.IO).launch {
//                startServer()
//            }
            }
        }
    }

    private fun startKitchenSink() {
        val packageName = "com.google.android.car.kitchensink"
        val className = "$packageName.SharingActivity2"
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            //            startActivity(launchIntent)
        } else {
            Log.d(TAG, "App not installed")
        }
        val intent = Intent()
        intent.setComponent(ComponentName(packageName, className))
        Log.d(TAG, "Creating activity")
        startActivity(intent)
    }

    private fun wannaFollowLinkDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("You got a Video Recommendation")
            .setMessage("The other user select Big Buck Bunny in the Road Reels app for you")
            .setPositiveButton(
                "Yeah! Let's see!",
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                    val rrPackageName = "com.example.android.cars.roadreels"
                    val intent = Intent(Intent.ACTION_VIEW)
                        .setClassName(rrPackageName, "$rrPackageName.MainActivity")
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .putExtra("detail", true)
                    startActivity(intent)
                    finish()
                })
            .setNegativeButton(
                "Don't bother me!",
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                    finish()
                })
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun makeSnack(view: View, text: String) =
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .setAnchorView(R.id.fab)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val result = super.onCreateOptionsMenu(menu)
        // Using findViewById because NavigationView exists in different layout files
        // between w600dp and w1240dp
        val navView: NavigationView? = findViewById(R.id.nav_view)
        if (navView == null) {
            // The navigation drawer already has the items including the items in the overflow menu
            // We only inflate the overflow menu if the navigation drawer isn't visible
            menuInflater.inflate(R.menu.overflow, menu)
        }
        return result
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.nav_settings)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun broadcastLinkSharingSong(snack: Snackbar) {
        snack.show()
        val intent = Intent("de.pacheco.PLAY_SONG")
        intent.putExtra("song_name", "Liedname")
        Log.d(TAG, "Sending Broadcast")
        sendBroadcast(intent)
    }

    private fun playSong(snack: Snackbar? = null) {
        snack?.show()
        val intent = Intent(Intent.ACTION_VIEW).apply {
// Das hier spielt nicht automatisch ab -.-                       Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH).apply {
//            putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Media.ENTRY_CONTENT_TYPE)
//            putExtra(MediaStore.EXTRA_MEDIA_ARTIST, "Calle 13")
//            putExtra(MediaStore.EXTRA_MEDIA_TITLE, "Atrévete-te-te")
//            putExtra(SearchManager.QUERY, "Calle 13 Atrévete-te-te")
            component = ComponentName("com.spotify.music", "com.spotify.music.MainActivity")
            setData(Uri.parse("spotify:track:0DJDDTRaX2uQVPJBRjcjsF"));
        }
        /* packageManager?.apply {
 //            if (intent.resolveActivity(this) != null) {
 //                startActivity(intent)
 //            }
 // nur für system apps           if (this.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
             val userHandle = Process.myUserHandle()
             mContext.startActivityAsUser(intent, userHandle);
 //            }
             val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
 //            val intent2: Intent = Intent(mContext, MainActivity::class.java)
             am.startActivityAsUser(intent, userHandle)
             startactivity
         }*/
    }
}