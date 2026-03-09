package com.osmiumai.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.UUID

object TestGenerationNotificationHelper {
    
    private const val CHANNEL_ID = "test_generation_channel"
    private const val NOTIFICATION_ID = 1001
    
    fun scheduleNotification(context: Context, delayMillis: Long) {
        createNotificationChannel(context)
        
        Handler(Looper.getMainLooper()).postDelayed({
            showNotification(context)
        }, delayMillis)
    }
    
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Test Generation"
            val descriptionText = "Notifications for AI-powered test generation"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun showNotification(context: Context) {
        val intent = Intent(context, MockTestActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_test)
            .setContentTitle("Your Mock Test is Ready! 🎉")
            .setContentText("Your AI-powered mock test has been generated successfully. Tap to start!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Your AI-powered mock test has been generated successfully based on your uploaded papers. Tap to start the test now!"))
            .build()
        
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, notification)
        }
        
        val appNotification = AppNotification(
            id = UUID.randomUUID().toString(),
            title = "Your Mock Test is Ready! 🎉",
            message = "Your AI-powered mock test has been generated successfully. Tap to start!",
            timestamp = System.currentTimeMillis()
        )
        NotificationStorageManager.addNotification(context, appNotification)
    }
}
