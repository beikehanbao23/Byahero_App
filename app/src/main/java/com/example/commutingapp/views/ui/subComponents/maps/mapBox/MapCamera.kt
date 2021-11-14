package com.example.commutingapp.views.ui.subComponents.maps.mapBox

import com.example.commutingapp.utils.others.Constants
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap

class MapCamera(private val mapBoxMap:MapboxMap?){


    fun move(latLng: LatLng, zoomLevel:Double, animationDuration:Int){
        mapBoxMap?.animateCamera(CameraUpdateFactory.newCameraPosition(buildCameraPosition(latLng,zoomLevel)), animationDuration)
    }
    private fun buildCameraPosition(latLng: LatLng, zoomLevel: Double): CameraPosition =
        CameraPosition.Builder()
            .target(latLng)
            .zoom(zoomLevel)
            .tilt(Constants.CAMERA_TILT_DEGREES)
            .build()

}