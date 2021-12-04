package com.example.commutingapp.views.ui.subComponents.maps

import android.content.Intent
import androidx.lifecycle.LiveData
import com.mapbox.mapboxsdk.geometry.LatLng

interface IMap<V> {


    fun moveCameraToUser(latLng: LatLng,zoomLevel:Double,cameraAnimationDuration:Int)
    fun getLocationSearchResult(requestCode: Int,resultCode: Int, data: Intent?)
    fun deleteRouteAndMarkers():Unit?
    fun pointMapMarker(latLng: LatLng)
    fun setupUI(mapType: String)
    fun getLastKnownLocation():LatLng?
    fun updateMapStyle(style:String)
    fun createLocationPuck()
    fun getMapView():V
    fun getPlaceText():LiveData<String?>
    fun getPlaceName():LiveData<String?>
    fun createDirections()
    fun clearCache()
}