package com.example.commutingapp.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.commutingapp.R
import com.example.commutingapp.data.others.Constants
import com.example.commutingapp.data.others.Notification
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TrackingNotification : LifecycleService(), Notification {

    lateinit var currentTrackingNotificationBuilder: NotificationCompat.Builder
    private val tracking = MutableLiveData<Boolean>()
    @Inject lateinit var baseTrackingNotificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        currentTrackingNotificationBuilder = baseTrackingNotificationBuilder
        TrackingService.is_Tracking.observe(this){
            tracking.value = it
        }

    }



    private fun getNotificationText() =
        if(tracking.value!!) "Pause" else "Resume"


    private fun getServicePendingIntent(): PendingIntent {
        val requestCode = if(tracking.value!!) Constants.REQUEST_CODE_PAUSE else Constants.REQUEST_CODE_RESUME
        return PendingIntent.getService(this,requestCode,getTrackingIntent(), FLAG_UPDATE_CURRENT)
    }

    private fun getTrackingIntent(): Intent {
        return Intent(this,TrackingService::class.java).apply {
            action = if(tracking.value!!) Constants.ACTION_PAUSE_SERVICE else Constants.ACTION_START_OR_RESUME_SERVICE
        }
    }

    override fun updateNotification(){

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        currentTrackingNotificationBuilder.javaClass.getDeclaredField("mAction").apply {
            isAccessible = true
            set(currentTrackingNotificationBuilder,ArrayList<NotificationCompat.Action>())
        }
        currentTrackingNotificationBuilder = baseTrackingNotificationBuilder
            .addAction(R.drawable.ic_baseline_pause_24, getNotificationText(), getServicePendingIntent())

        notificationManager.notify(Constants.NOTIFICATION_ID,currentTrackingNotificationBuilder.build())



    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun createNotificationChannel() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            notificationManager.createNotificationChannel(this)
        }


    }



}