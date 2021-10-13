package com.example.commutingapp.data.service

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Looper
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

    @Inject  lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isFirstRun = true
    @Inject lateinit var baseTrackingNotificationBuilder: NotificationCompat.Builder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        receiveActionCommand(intent)
        return super.onStartCommand(intent, flags, startId)
    }


    companion object{
    val polyLineCreator:PolyLineCreator = PolyLineCreator()
    val is_Tracking = MutableLiveData<Boolean>()
    private val liveDataOuterPolyline = polyLineCreator.polyLine()

    }

    fun isCurrentlyTracking(): LiveData<Boolean> = is_Tracking
    fun outerPolyline(): MutableLiveData<outerPolyline> = liveDataOuterPolyline

    private fun postInitialValues(){
        is_Tracking.postValue(false)
        liveDataOuterPolyline.postValue(mutableListOf())
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


    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult?) {
            if(is_Tracking.value!!){
                locationResult ?: return
                    for (location in locationResult.locations) {
                        polyLineCreator.addPolyline(location)
                    }
                }
            }
    }



    private fun createLocationRequest() {

        if(!is_Tracking.value!!){
            fusedLocationClient.removeLocationUpdates(locationCallback)
            return
        }
        requestLocationUpdates()

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
        polyLineCreator.addEmptyPolyLines()//TODO
        is_Tracking.postValue(true)//TODo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification().createNotificationChannel()
        }

        startForeground(NOTIFICATION_ID,
         baseTrackingNotificationBuilder.build()
        )

    }



    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates(){
        if(hasLocationPermission(this)){
            fusedLocationClient.requestLocationUpdates(
                CommuterFragment().locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }


}