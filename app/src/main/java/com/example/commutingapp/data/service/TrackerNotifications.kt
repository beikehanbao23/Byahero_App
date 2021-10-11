package com.example.commutingapp.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.commutingapp.R
import com.example.commutingapp.data.others.Constants.NOTIFICATION_CHANNEL_ID
import com.example.commutingapp.data.others.Constants.NOTIFICATION_CHANNEL_NAME


class TrackerNotification :Notification<NotificationCompat.Builder>{
    @RequiresApi(Build.VERSION_CODES.O)
    override fun createChannelNotification(notificationManager: NotificationManager){

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }


    override fun createNotification(context:Context,pendingIntent: PendingIntent?):NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_jeep)
            .setContentTitle("Commuting")
            .setContentText("Recording your commute.")
            .setContentIntent(pendingIntent)

    }

}