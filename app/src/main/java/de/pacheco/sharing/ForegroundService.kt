package de.pacheco.sharing

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.util.regex.Pattern

class ForegroundService : Service() {

    private val CHANNEL_ID = "ServerChannel"
    private val TAG = ForegroundService::class.java.simpleName

    override fun onCreate() {
        Log.d(TAG,"onCreate")
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification("Server läuft..."))
        CoroutineScope(Dispatchers.IO).launch {
            startServer()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startServer() {
        val serverSocket = ServerSocket(12345)
        while (true) {
            Log.d(TAG,"Server running, waiting for input")
            val clientSocket = serverSocket.accept()
            val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            val message = input.readLine()
            val jsonObject = JSONObject(message)
            val action = jsonObject.getString("action")
            val data = jsonObject.getString("data")
            val text = jsonObject.getString("text")
            val serve = jsonObject.getBoolean("serve")
            val url = extractUrl(text)
            Log.d(TAG,"Text: $text Aktion: $action, Daten: $data, URL: $url")
            if (serve){
                val activityIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                Handler(Looper.getMainLooper()).post {
                    startActivity(activityIntent)
                }
            }else{
                showNotification(text,url)
            }
            clientSocket.close()
        }
    }

    private fun createNotification(content: String): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE) //Flag wurde mir vorgeschlagen als 0 von Copilot

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Server App")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun showNotification(content: String, url: String?) {
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.netflix.com"))
        val intent = if (url != null) {
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
        } else {
            Intent(this, MainActivity::class.java)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Server App")
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Server Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
    private fun extractUrl(text: String): String? {
        val urlPattern = Pattern.compile(
            "(https?://[\\w-]+(\\.[\\w-]+)+[/#?]?.*)",
            Pattern.CASE_INSENSITIVE
        )
        val matcher = urlPattern.matcher(text)
        return if (matcher.find()) {
            matcher.group(1)
        } else {
            null
        }
    }
}