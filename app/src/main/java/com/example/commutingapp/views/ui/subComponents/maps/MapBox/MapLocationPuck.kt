package com.example.commutingapp.views.ui.subComponents.maps.MapBox

import android.annotation.SuppressLint
import android.app.Activity

import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style

class MapLocationPuck(private val activity: Activity, private val mapBoxMap: MapboxMap?) {

     fun buildLocationPuck( style: Style){
        LocationComponentOptions.builder(activity)
            .accuracyAlpha(0.3f)
            .compassAnimationEnabled(true)
            .accuracyAnimationEnabled(true)
            .build().also { componentOptions ->
                mapBoxMap?.locationComponent?.apply {
                    activateLocationComponent(
                        LocationComponentActivationOptions.builder(activity, style!!)
                            .locationComponentOptions(componentOptions)
                            .build()
                    )
                    createComponentsLocation(this)

                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun createComponentsLocation(locationComponent: LocationComponent) {
        locationComponent.apply {
            isLocationComponentEnabled = true
            renderMode = RenderMode.COMPASS
        }
    }

}