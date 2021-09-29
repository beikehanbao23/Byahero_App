package com.example.commutingapp.data.service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.commutingapp.data.others.Constants.ACTION_PAUSE_SERVICE
import com.example.commutingapp.data.others.Constants.ACTION_SHOW_COMMUTER_FRAGMENT
import com.example.commutingapp.data.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.commutingapp.data.others.Constants.ACTION_STOP_SERVICE
import com.example.commutingapp.data.others.Constants.FASTEST_LOCATION_UPDATE_INTERVAL
import com.example.commutingapp.data.others.Constants.NORMAL_LOCATION_UPDATE_INTERVAL
import com.example.commutingapp.data.others.Constants.NOTIFICATION_ID
import com.example.commutingapp.data.others.TrackingPermissionUtility
import com.example.commutingapp.data.others.TrackingPermissionUtility.hasLocationPermission
import com.example.commutingapp.views.ui.activities.MainScreen
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber

typealias polyline = MutableList<LatLng>
typealias polylines= MutableList<polyline>


class TrackingService : LifecycleService() {
    private val notifications: Notifications<NotificationCompat.Builder> = TrackerNotification()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isFirstRun = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startActionCommand(intent)
        return super.onStartCommand(intent, flags, startId)
    }


    companion object{
    val isTracking = MutableLiveData<Boolean>()
    val pathPoints = MutableLiveData<polylines>()

    }
    private fun postInitialValues(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf()    )
    }
    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        isTracking.observe(this){
            createLocationRequest(it)
        }
    }

    private fun addEmptyPolylines() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)//TODO try to remove
    }?: pathPoints.postValue(mutableListOf(mutableListOf()))


    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if(isTracking.value!!){
                result?.locations?.let {
                    for (location in it){
                    addPathPoint(location)
                    }
                }
            }
        }
    }

    private fun addPathPoint(location:Location?){
        location?.let {
            val position = LatLng(location.latitude,location.longitude)
            pathPoints.value?.apply {
                last().add(position)
                pathPoints.postValue(this)
            }
        }
    }
    @SuppressLint("MissingPermission")
    fun createLocationRequest(isTracking:Boolean) {
        if(isTracking && hasLocationPermission(this)){
        val locationRequest = LocationRequest.create().apply {
            interval = NORMAL_LOCATION_UPDATE_INTERVAL
            fastestInterval = FASTEST_LOCATION_UPDATE_INTERVAL
            priority = PRIORITY_HIGH_ACCURACY
        }
            fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper()
                )
        }


        if(!isTracking){
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
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
        addEmptyPolylines()
        isTracking.postValue(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        startForeground(NOTIFICATION_ID,
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