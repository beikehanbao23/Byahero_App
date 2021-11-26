package com.example.commutingapp.views.ui.subComponents.maps.mapBox

import android.annotation.SuppressLint
import android.app.Activity
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.Style

class MapLocationPuck(private val activity: Activity, private val locationComponent: LocationComponent?) {

      fun buildLocationPuck(style: Style){

              getLocationComponentOptions().also { componentOptions ->
                  locationComponent?.apply {

                      activateLocationComponent(
                          LocationComponentActivationOptions.builder(activity, style)
                              .locationComponentOptions(componentOptions)
                              .build()
                      )
                      createComponentsLocation(this)
                  }

          }

    }
    private fun getLocationComponentOptions() = LocationComponentOptions.builder(activity)
            .accuracyAlpha(0.3f)
            .compassAnimationEnabled(true)
            .accuracyAnimationEnabled(true)
            .build()

    @SuppressLint("MissingPermission")
    private fun createComponentsLocation(locationComponent: LocationComponent) {
        locationComponent.apply {
            isLocationComponentEnabled = true
            renderMode = RenderMode.COMPASS
        }
    }

}