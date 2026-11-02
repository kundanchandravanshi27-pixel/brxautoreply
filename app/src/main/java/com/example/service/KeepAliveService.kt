package com.example.service

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class KeepAliveService : Service() {

    companion object {
        private const val CHANNEL_ID = "bizryno_status"
        private const val NOTIF_ID = 1001
        const val ACTION_TOGGLE_AI = "com.bizryno.autoreply.ACTION_TOGGLE_AI"
        
        private val _isServiceRunning = MutableStateFlow(false)
        val isServiceRunning = _isServiceRunning.asStateFlow()
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_TOGGLE_AI) {
            val broadcastIntent = Intent("com.bizryno.autoreply.TOGGLE_AI_BROADCAST")
            sendBroadcast(broadcastIntent)
        }
        
        val notification = buildNotification("AI Active & Listening")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIF_ID, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIF_ID, notification)
        }
        _isServiceRunning.value = true
        return START_STICKY
    }

    override fun onDestroy() {
        _isServiceRunning.value = false
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "BizRyno Keep-Alive Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps BizRyno Auto Reply running in background"
                setSound(null, null)
                enableVibration(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(statusText: String): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            this, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val toggleIntent = Intent(this, KeepAliveService::class.java).apply {
            action = ACTION_TOGGLE_AI
        }
        val togglePendingIntent = PendingIntent.getService(
            this, 1, toggleIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("BizRyno Auto Reply")
            .setContentText(statusText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(openPendingIntent)
            .addAction(0, "Toggle AI", togglePendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
