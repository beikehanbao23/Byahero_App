package com.example.commutingapp.views.ui.subComponents.maps.mapBox

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import com.example.commutingapp.BuildConfig
import com.example.commutingapp.utils.others.Constants.SEARCH_DIALOG_LAYOUT_COLOR
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions


class MapSearch(private val activity:Activity) {

    fun getLocationSearchResult(data: Intent?): LatLng {
         val resultDestination = PlaceAutocomplete.getPlace(data)
        return LatLng(
            (resultDestination.geometry() as Point).latitude(),
            (resultDestination.geometry() as Point).longitude()
        )
    }



    fun getLocationSearchIntent(): Intent {
        return PlaceAutocomplete.IntentBuilder()
            .accessToken(BuildConfig.MAPBOX_DOWNLOADS_TOKEN)
            .placeOptions(buildPlaceOptions())
            .build(activity)
    }
    private fun buildPlaceOptions() =  PlaceOptions.builder()
        .backgroundColor(Color.parseColor(SEARCH_DIALOG_LAYOUT_COLOR))
        .limit(10)
        .build(PlaceOptions.MODE_CARDS)



}