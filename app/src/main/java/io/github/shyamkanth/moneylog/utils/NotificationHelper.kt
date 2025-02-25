package io.github.shyamkanth.moneylog.utils

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import io.github.shyamkanth.moneylog.R
import io.github.shyamkanth.moneylog.ui.MainActivity

object NotificationHelper {
    private const val CHANNEL_ID = "CHANNEL_ID"
    private lateinit var notificationManager: NotificationManager

    fun init(context: Context) {
        if (!::notificationManager.isInitialized) {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            createNotificationChannel(context)
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "App Notification", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for app notification"
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(
        context: Context,
        title: String,
        message: String,
        messageLong: String = message,
        icon: Int,
        targetActivity: Class<out Activity>,
        intentArgs: Int = 0,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        val intent = Intent(context, targetActivity).apply {
            if (intentArgs != 0) {
                putExtra("monthId", intentArgs)
            }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 🔹 "Mark as Done" Action (Handled by a BroadcastReceiver)
        val doneIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "MARK_AS_DONE"
        }
        val donePendingIntent = PendingIntent.getBroadcast(
            context, 1, doneIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context, 2, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageLong))
            .addAction(R.drawable.ic_wallet, "Mark as done", donePendingIntent)
            .addAction(R.drawable.ic_wallet, "View in app", openAppPendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}

/**

NotificationHelper.sendNotification(
    context = applicationContext,
    title = "your notification title",
    message = "your notification content",
    messageLong = "your long notification content (if any or just remove this argument)",
    icon = R.drawable.ic_wallet,
    targetActivity = TargetActivity::class.java,
    intentArgs = newMonthId.toInt()
)

 */