package com.ivy.wallet.system.notification

import android.content.Context
import androidx.core.app.NotificationCompat

class IvyNotification(
    context: Context,
    val ivyChannel: IvyNotificationChannel
) : NotificationCompat.Builder(context, ivyChannel.channelId)