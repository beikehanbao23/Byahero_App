package com.example.commutingapp.data.others

interface Notification {
    fun createNotification()
    fun updateNotification(contentText:String)
    fun createNotificationChannel()
}