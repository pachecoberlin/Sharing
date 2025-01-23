package de.pacheco.sharing

import android.app.AlertDialog
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.UserManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
    val TAG: String = "PachecoSharingAppMainActivity"

    //    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "intent: $intent")
        val linksharing = intent.getBooleanExtra("linksharing", false)
        Log.d(TAG, "linksharing: $linksharing")
        if (linksharing) {
            wannaFollowLinkDialog()
        } else {
            // Replace with the package name of the app you want to open
            val packageName = "com.google.android.car.kitchensink"
            val className = "$packageName.KitchenSink2Activity"
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
            finish()
        }

        // Close this app after attempting to open the other app

        /*  Log.d(TAG, "Creating activity")
          intent.apply {
              if (action == "de.pacheco.PLAY_SONG") {
                  Log.d(TAG, "No just playing song and returning")
                  playSong()
                  return
              }
          }

          if (true) return


          super.onCreate(savedInstanceState)
          binding = ActivityMainBinding.inflate(layoutInflater)
          setContentView(binding.root)
          setSupportActionBar(binding.appBarMain.toolbar)

          binding.appBarMain.fab?.setOnClickListener { view ->
  //            playSong(makeSnack(view, "Playing song on Spotify"))
              broadcastLinkSharingSong(makeSnack(view, "Broadcast"))
          }
          val navHostFragment =
              (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?)!!
          val navController = navHostFragment.navController

          binding.navView?.let {
              appBarConfiguration = AppBarConfiguration(
                  setOf(
                      R.id.nav_transform, R.id.nav_reflow, R.id.nav_slideshow, R.id.nav_settings
                  ),
                  binding.drawerLayout
              )
              setupActionBarWithNavController(navController, appBarConfiguration)
              it.setupWithNavController(navController)
          }

          binding.appBarMain.contentMain.bottomNavView?.let {
              appBarConfiguration = AppBarConfiguration(
                  setOf(
                      R.id.nav_transform, R.id.nav_reflow, R.id.nav_slideshow
                  )
              )
              setupActionBarWithNavController(navController, appBarConfiguration)
              it.setupWithNavController(navController)
          }*/
    }
    private fun wannaFollowLinkDialog(){
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