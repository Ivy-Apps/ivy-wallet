package com.ivy.notifications

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.ivy.resources.R

class NotificationService(
    private val context: Context
) {

    fun defaultIvyNotification(
        channel: IvyNotificationChannel,
        autoCancel: Boolean = true,
        priority: Int = NotificationCompat.PRIORITY_HIGH
    ): IvyNotification {
        val ivyNotification = IvyNotification(context, channel)
        val color = ContextCompat.getColor(context, R.color.green)
        ivyNotification.setSmallIcon(R.drawable.ic_notification)
            .setColor(color)
            .setPriority(priority)
            .setColorized(true)
            .setLights(color, 1000, 200)
            .setAutoCancel(autoCancel)
        return ivyNotification
    }


    fun showNotification(
        notification: NotificationCompat.Builder,
        notificationId: Int
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Register the channel with the system
            val channel = (notification as IvyNotification).ivyChannel.create(context)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(notificationId, notification.build())
    }

    fun dismissNotification(notificationId: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }
}