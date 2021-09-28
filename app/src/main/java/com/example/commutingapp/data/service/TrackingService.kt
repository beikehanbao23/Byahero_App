package com.example.commutingapp.data.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.example.commutingapp.data.others.Constants.ACTION_PAUSE_SERVICE
import com.example.commutingapp.data.others.Constants.ACTION_SHOW_COMMUTER_FRAGMENT
import com.example.commutingapp.data.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.commutingapp.data.others.Constants.ACTION_STOP_SERVICE
import com.example.commutingapp.data.others.Constants.NOTIFICATION_ID
import com.example.commutingapp.views.ui.activities.MainScreen
import timber.log.Timber

class TrackingService : LifecycleService() {
    private val notifications: Notifications<NotificationCompat.Builder> = TrackerNotification()

    private var isFirstRun = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startActionCommand(intent)
        return super.onStartCommand(intent, flags, startId)
    }
    private fun startActionCommand(intent:Intent?){
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                        return@let
                    }
                    Timber.e("Resumed")
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.e("Paused")
                }
                ACTION_STOP_SERVICE -> {
                    Timber.e("Stopped")
                }
            }
        }
    }
    private fun startForegroundService() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        startForeground(
            NOTIFICATION_ID,
            notifications.createNotification(
                baseContext,
                getMainActivityPendingIntent()
            ).build()
        )

    }
    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainScreen::class.java).also {
            it.action = ACTION_SHOW_COMMUTER_FRAGMENT
        },
        FLAG_UPDATE_CURRENT

    )
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifications.apply {
            createChannelNotifications(notificationManager)
        }
    }

}