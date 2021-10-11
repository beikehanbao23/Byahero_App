package com.example.commutingapp.data.service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location

import android.os.Build
import android.os.Looper
import android.util.Log

import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.commutingapp.data.others.Constants.ACTION_PAUSE_SERVICE
import com.example.commutingapp.data.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.commutingapp.data.others.Constants.ACTION_STOP_SERVICE
import com.example.commutingapp.data.others.Constants.NOTIFICATION_ID
import com.example.commutingapp.data.others.TrackingPermissionUtility.hasLocationPermission
import com.example.commutingapp.views.ui.fragments.CommuterFragment
import com.google.android.gms.location.*
import com.mapbox.mapboxsdk.geometry.LatLng
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

typealias innerPolyline = MutableList<LatLng>
typealias outerPolyline = MutableList<innerPolyline>

@AndroidEntryPoint
class TrackingService : LifecycleService() {
    private val notification: Notification<NotificationCompat.Builder> = TrackerNotification()
    @Inject  lateinit var fusedLocationClient: FusedLocationProviderClient
    @Inject  lateinit var mainActivityPendingIntent: PendingIntent
    private var isFirstRun = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        receiveActionCommand(intent)
        return super.onStartCommand(intent, flags, startId)
    }


    companion object{
    private val is_Tracking = MutableLiveData<Boolean>()
    private val liveDataOuterPolyline = MutableLiveData<outerPolyline>()

    }

    fun isTracking(): LiveData<Boolean> = is_Tracking
    fun outerPolyline(): MutableLiveData<outerPolyline> = liveDataOuterPolyline

    private fun postInitialValues(){
        is_Tracking.postValue(false)
        liveDataOuterPolyline.postValue(mutableListOf()    )
    }
    @SuppressLint("VisibleForTests")
    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        is_Tracking.observe(this){
            createLocationRequest()
        }
    }
    private fun pauseService(){
        is_Tracking.postValue(false)
    }
    private fun addEmptyPolyLines() = liveDataOuterPolyline.value?.apply {
        add(mutableListOf())
        liveDataOuterPolyline.postValue(this)
    }?: liveDataOuterPolyline.postValue(mutableListOf(mutableListOf()))


    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult?) {
            if(is_Tracking.value!!){
                locationResult ?: return
                    for (location in locationResult.locations) {
                        addPolyline(location)
                        Log.e("Status:", "Lat: ${location.latitude}, Long:${location.longitude}")
                    }
                }
            }
    }

    private fun addPolyline(location: Location?){
        location?.let {
            val position = LatLng(location.latitude,location.longitude)
            liveDataOuterPolyline.value?.apply {
                last().add(position)
                liveDataOuterPolyline.postValue(this)
            }
        }
    }
    @SuppressLint("MissingPermission")
    fun createLocationRequest() {

        if(!is_Tracking.value!!){
            fusedLocationClient.removeLocationUpdates(locationCallback)
            return
        }

        if(hasLocationPermission(this)){
            fusedLocationClient.requestLocationUpdates(
                CommuterFragment().locationRequest,
                locationCallback,
                Looper.getMainLooper()
                )
        }



    }






    private fun receiveActionCommand(intent:Intent?){
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                        return@let
                    }
                    startForegroundService()//TODO fix later
                    Timber.e("Resumed")
                }
                ACTION_PAUSE_SERVICE -> {
                  pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.e("Stopped")
                }
            }
        }
    }
    private fun startForegroundService() {
        addEmptyPolyLines()
        is_Tracking.postValue(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        startForeground(NOTIFICATION_ID,
                notification.createNotification(
                baseContext,
                mainActivityPendingIntent
            ).build()
        )

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notification.apply {
            createChannelNotification(notificationManager)
        }
    }

}