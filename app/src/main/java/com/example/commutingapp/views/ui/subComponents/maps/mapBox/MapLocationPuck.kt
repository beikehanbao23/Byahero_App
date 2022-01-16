package com.example.commutingapp.views.ui.subComponents.maps.mapBox

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.coroutines.coroutineScope

class MapLocationPuck(private val activity: Activity, private val locationComponent: LocationComponent?) {

      suspend fun buildLocationPuck(style: Style) {
          coroutineScope {
              getLocationComponentOptions().also { componentOptions ->
                  locationComponent?.apply {
                      activateLocationComponent(
                          LocationComponentActivationOptions.builder(activity, style)
                              .locationComponentOptions(componentOptions)
                              .build())
                      createComponentsLocation(this)
                  }
              }
          }
      }
    private fun getLocationComponentOptions() = LocationComponentOptions.builder(activity)
            .accuracyAlpha(0.3f)
            .compassAnimationEnabled(true)
            .accuracyAnimationEnabled(true)
            .elevation(100.0f)
            .compassAnimationEnabled(true)
            .build()


    private fun createComponentsLocation(locationComponent: LocationComponent) {
        if (ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
           && ActivityCompat.checkSelfPermission(activity, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           return
        }
        locationComponent.apply {
            isLocationComponentEnabled = true
            renderMode = RenderMode.COMPASS
        }
    }

}