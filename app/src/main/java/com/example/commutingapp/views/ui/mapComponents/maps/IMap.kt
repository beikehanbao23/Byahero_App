package com.example.commutingapp.views.ui.mapComponents.maps

import android.content.Intent
import android.os.Bundle
import com.mapbox.mapboxsdk.geometry.LatLng

interface IMap<T> {

    fun getMapInstance():T?
    fun moveCameraToUser(latLng: LatLng,zoomLevel:Double,cameraAnimationDuration:Int)
    fun getLocationSearchResult(requestCode: Int,resultCode: Int, data: Intent?)
    fun deleteAllMapMarker()
    fun pointMapMarker(latLng: LatLng)
    fun setupMap(savedInstanceBundle:Bundle?,mapType:String)
    fun recoverMissingMapMarker()
    fun getLastKnownLocation():LatLng?
    fun updateMapStyle(style:String)
    fun createLocationPuck()
}