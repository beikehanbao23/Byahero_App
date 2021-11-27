package com.example.commutingapp.views.ui.subComponents.maps

import android.content.Intent
import com.mapbox.mapboxsdk.geometry.LatLng

interface IMap<V> {


    fun moveCameraToUser(latLng: LatLng,zoomLevel:Double,cameraAnimationDuration:Int)
    fun getLocationSearchResult(requestCode: Int,resultCode: Int, data: Intent?)
    fun deleteAllMapMarker():Unit?
    fun pointMapMarker(latLng: LatLng)
    fun setupUI(mapType: String)
    fun getLastKnownLocation():LatLng?
    fun updateMapStyle(style:String)
    fun createLocationPuck()
    fun getMapView():V
    fun createDirections()
}