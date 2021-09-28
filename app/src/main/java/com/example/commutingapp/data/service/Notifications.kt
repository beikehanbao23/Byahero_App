package com.example.commutingapp.data.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context

interface Notifications<T> {
    fun createNotification(context:Context,pendingIntent: PendingIntent?):T
    fun createChannelNotifications(notificationManager: NotificationManager)
}