package com.example.commutingapp.utils.others

interface Notification {
    fun createNotification()
    fun updateNotification(contentText:String)
    fun createNotificationChannel()
}