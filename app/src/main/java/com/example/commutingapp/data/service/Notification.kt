package com.example.commutingapp.data.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context

interface Notification<T> {
    fun createNotification(context:Context,pendingIntent: PendingIntent?):T
    fun createChannelNotification(notificationManager: NotificationManager)
}