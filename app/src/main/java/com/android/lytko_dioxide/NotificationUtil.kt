package com.android.lytko_dioxide

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.kirich1409.androidnotificationdsl.Notification
import com.kirich1409.androidnotificationdsl.notification

object NotificationUtil {
    
    private var notificationId = 0
    
    fun showExceededValueNotification(context: Context, value: Float) {
        
        val id = notificationId++
        
        // Create intent for notification onClick behaviour
        val pendingView = pendingIntent(context, id, Intent(context, MainActivity::class.java))
        
        // Build and show notification
        createChannel(context)
        showNotification(
            context,
            id,
            notification(context, "default", smallIcon = R.drawable.ic_error) {
        
                val title = "Опасный уровень CO₂"
                val text = "В комнате $value ppm, проветрите помещение"
                
                // Default notification setup
                style(NotificationCompat.BigTextStyle().bigText(text))
                contentTitle(title)
                contentText(text)
                ticker(title)
                contentIntent(pendingView)
                color(Color.RED)
                priority(NotificationCompat.PRIORITY_HIGH)
        
                sound(Uri.parse("content://settings/system/notification_sound"))
            }
        )
    }
    
    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                "default",
                "Default",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Warnings"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun showNotification(
        context: Context,
        id: Int,
        notification: android.app.Notification
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, notification)
    }
    
    private fun pendingIntent(
        context: Context,
        id: Int,
        intent: Intent
    ) = PendingIntent.getActivity(
        context,
        id,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}