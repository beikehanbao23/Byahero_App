package com.example.commutingapp.views.ui.subComponents.maps

import android.content.Intent
import android.os.Bundle
import com.example.commutingapp.views.ui.subComponents.FAB.FloatingActionButtonMapType
import com.mapbox.mapboxsdk.geometry.LatLng

interface IMap<T> {

    fun getMapInstance():T?
    fun moveCameraToUser(latLng: LatLng,zoomLevel:Double,cameraAnimationDuration:Int)
    fun getLocationSearchResult(requestCode: Int,resultCode: Int, data: Intent?)
    fun deleteAllMapMarker():Unit?
    fun pointMapMarker(latLng: LatLng)
    fun setupMap(savedInstanceBundle:Bundle?)
    fun setupUI(mapType: String)
    fun recoverMissingMapMarker()
    fun getLastKnownLocation():LatLng?
    fun updateMapStyle(style:String)
    fun initializeLocationPuck()
}