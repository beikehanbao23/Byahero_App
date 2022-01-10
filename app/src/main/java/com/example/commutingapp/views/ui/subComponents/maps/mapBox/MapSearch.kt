package com.example.commutingapp.views.ui.subComponents.maps.mapBox

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.Constants.SEARCH_DIALOG_LAYOUT_COLOR
import com.google.gson.JsonObject
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions


class MapSearch(private val activity:Activity,style: Style?) {
    private lateinit var home: CarmenFeature
    private lateinit var work: CarmenFeature



    init {
        initializeUserLocations()
    }
    fun getLocationSearchResult(data: Intent?): LatLng {
         val resultDestination = PlaceAutocomplete.getPlace(data)
        return LatLng(
            (resultDestination.geometry() as Point).latitude(),
            (resultDestination.geometry() as Point).longitude()
        )
    }



    fun getLocationSearchIntent(): Intent {
        return PlaceAutocomplete.IntentBuilder()
            .accessToken(activity.getString(R.string.MapsToken))
            .placeOptions(buildPlaceOptions())
            .build(activity)
    }
    private fun buildPlaceOptions() =  PlaceOptions.builder()
        .backgroundColor(Color.parseColor(SEARCH_DIALOG_LAYOUT_COLOR))
        .limit(10)
        .addInjectedFeature(home)
        .addInjectedFeature(work)
        .build(PlaceOptions.MODE_CARDS)

    private fun initializeUserLocations() {
        home = CarmenFeature.builder()
            .text("Mapbox SF Office")
            .geometry(Point.fromLngLat(-122.3964485, 37.7912561))
            .placeName("50 Beale St, San Francisco, CA")
            .properties(JsonObject())
            .build()
        work = CarmenFeature.builder().text("Mapbox DC Office")
            .placeName("740 15th Street NW, Washington DC")
            .geometry(Point.fromLngLat(-77.0338348, 38.899750))
            .properties(JsonObject())
            .build()
    }

}