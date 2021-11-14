package com.example.commutingapp.data.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.Constants
import com.example.commutingapp.utils.others.Constants.ACTION_PAUSE_SERVICE
import com.example.commutingapp.utils.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.commutingapp.utils.others.Constants.ACTION_STOP_SERVICE
import com.example.commutingapp.utils.others.Constants.NOTIFICATION_CHANNEL_ID
import com.example.commutingapp.utils.others.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.commutingapp.utils.others.Constants.NOTIFICATION_ID
import com.example.commutingapp.utils.others.TrackingPermissionUtility.hasLocationPermission
import com.example.commutingapp.utils.others.WatchFormatter
import com.example.commutingapp.views.ui.fragments.CommuterFragment
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
open class TrackingService : LifecycleService() {

    @Inject lateinit var fusedLocationClient: FusedLocationProviderClient
    @Inject lateinit var baseTrackingNotificationBuilder:NotificationCompat.Builder
    lateinit var currentTrackingNotificationBuilder: NotificationCompat.Builder
    private var isFirstRun = true


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        receiveActionCommand(intent)
        return super.onStartCommand(intent, flags, startId)
    }


    companion object {
        private val trackingPolyLine: TrackingPolyLine = TrackingPolyLine()
        private val outerPolyline = trackingPolyLine.polyLine()
        private val stopWatch = TrackingStopWatch()
        val is_Tracking = MutableLiveData<Boolean>()
        val timeInMillis = stopWatch.getTimeRunMillis()

    }

    fun isCurrentlyTracking(): LiveData<Boolean> = is_Tracking
    fun outerPolyline(): MutableLiveData<alias_outerPolyline> = outerPolyline

    private fun postInitialValues() {
        is_Tracking.postValue(false)
        outerPolyline.postValue(mutableListOf())
        stopWatch.postInitialValues()
    }

    override fun onCreate() {
        super.onCreate()
        currentTrackingNotificationBuilder = baseTrackingNotificationBuilder
        postInitialValues()
        subscribeToObservers()


    }
    private fun subscribeToObservers(){
        is_Tracking.observe(this) {
            createLocationRequest()
            createNotification()
        }

    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            if (is_Tracking.value!!) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    trackingPolyLine.addPolyline(location)
                }
            }
        }
    }


    private fun createLocationRequest() {

        if (!is_Tracking.value!!) {
            removeLocationUpdates()
            return
        }
        requestLocationUpdates()
    }


    private fun receiveActionCommand(intent: Intent?) {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        startService()
                        isFirstRun = false // todo fix startTimer
                        return@let
                    }
                    startService()
                }
                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                  stopForegroundService()
                }
            }
        }
    }


    private fun startService() {
        trackingPolyLine.addEmptyPolyLines()
        is_Tracking.postValue(true)
        stopWatch.start()
    }
    private fun pauseService() {
        is_Tracking.postValue(false)
        stopWatch.pause()
    }

    private fun stopForegroundService(){
        if (androidVersionIsOreo()) {
            destroyNotificationChannel()
        }
        stopForeground(true)
    }
    private fun startForegroundService() {

        if (androidVersionIsOreo()) {
            createNotificationChannel()
        }

        startForeground(NOTIFICATION_ID, baseTrackingNotificationBuilder.build())
        stopWatch.getTimeCommuteInSeconds().observe(this) {
            updateNotification(WatchFormatter.getFormattedStopWatchTime(it*1000L))
        }

    }
    private fun androidVersionIsOreo() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        if (hasLocationPermission(this)) {
            fusedLocationClient.requestLocationUpdates(
                CommuterFragment().locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun removeLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


     private fun createNotification() {
        refreshNotificationActions()
        addNotificationAction()
        postNotification()
    }

     private fun updateNotification(contentText:String) {

        currentTrackingNotificationBuilder.setContentText(contentText)
        postNotification()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        getNotificationChannel().apply {
            getNotificationManager().createNotificationChannel(this)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun destroyNotificationChannel(){
        getNotificationManager().deleteNotificationChannel(NOTIFICATION_CHANNEL_ID)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNotificationChannel() =
        NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )













    private fun getNotificationManager() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun getNotificationText() = if (is_Tracking.value!!) "Pause" else "Resume"

    private fun getServicePendingIntent(): PendingIntent {
        val requestCode =
            if (is_Tracking.value!!) Constants.REQUEST_CODE_PAUSE else Constants.REQUEST_CODE_RESUME
        return PendingIntent.getService(this, requestCode, getTrackingIntent(), FLAG_UPDATE_CURRENT)
    }

    private fun getTrackingIntent(): Intent {
        return Intent(this, TrackingService::class.java).apply {
            action = if (is_Tracking.value!!) ACTION_PAUSE_SERVICE else ACTION_START_OR_RESUME_SERVICE
        }
    }

    private fun postNotification() {
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        notificationManager.apply {
            notify(NOTIFICATION_ID, currentTrackingNotificationBuilder.build())
        }
    }

    private fun addNotificationAction() {
        currentTrackingNotificationBuilder = baseTrackingNotificationBuilder
            .addAction(
                R.drawable.ic_baseline_pause_24,
                getNotificationText(),
                getServicePendingIntent()
            )
    }

    private fun refreshNotificationActions() {
        currentTrackingNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentTrackingNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }
    }


}