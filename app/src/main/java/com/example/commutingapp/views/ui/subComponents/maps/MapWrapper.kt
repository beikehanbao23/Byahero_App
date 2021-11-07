package com.example.commutingapp.views.ui.subComponents.maps

import android.content.Intent
import android.os.Bundle
import com.example.commutingapp.views.ui.subComponents.FAB.FloatingActionButtonMapType
import com.mapbox.mapboxsdk.geometry.LatLng

class MapWrapper<T>(private val map:IMap<T>) {

    fun getMapInstance():T?{
        return map.getMapInstance()
    }
    fun moveCameraToUser(latLng: LatLng, zoomLevel:Double, cameraAnimationDuration:Int){
        map.moveCameraToUser(latLng, zoomLevel, cameraAnimationDuration)
    }
    fun getLocationSearchResult(requestCode: Int,resultCode: Int, data: Intent?){
        map.getLocationSearchResult(requestCode, resultCode, data)
    }
    fun deleteAllMapMarker(){
        map.deleteAllMapMarker()
    }
    fun pointMapMarker(latLng: LatLng){
        map.pointMapMarker(latLng)
    }
    fun setupMap(savedInstanceBundle: Bundle?){
        map.setupMap(savedInstanceBundle)
    }
    fun setupUI(mapType: String){
        map.setupUI(mapType)
    }
    fun recoverMissingMapMarker(){
        map.recoverMissingMapMarker()
    }
    fun getLastKnownLocation(): LatLng?{
        return map.getLastKnownLocation()
    }
    fun updateMapStyle(style:String){
        map.updateMapStyle(style)
    }
    fun createLocationPuck(){
        map.initializeLocationPuck()
    }
}