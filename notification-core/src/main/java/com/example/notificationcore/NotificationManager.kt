package com.example.notificationcore

import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationManager(private val context: Context) {
    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                AndroidNotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(title: String, message: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    companion object {
        private const val CHANNEL_ID = "notification_channel"
        private const val CHANNEL_NAME = "Notificações"
        private const val CHANNEL_DESCRIPTION = "Canal para notificações do aplicativo"
        private const val NOTIFICATION_ID = 1
    }
} 