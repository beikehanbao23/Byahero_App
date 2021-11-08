package com.example.commutingapp.views.ui.subComponents.maps.MapBox

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.Constants
import com.google.gson.JsonObject
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.style.layers.PropertyValue
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

class MapSearch(private val activity:Activity,private val style: Style?) {
    private lateinit var home: CarmenFeature
    private lateinit var work: CarmenFeature
    init {
        initializeUserLocations()
    }

    fun getLocationSearchResult(requestCode: Int,resultCode: Int, data: Intent?):LatLng?{
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_AUTOCOMPLETE) {
            val selectedCarmenFeature = PlaceAutocomplete.getPlace(data)
            style?.let { style->
                val source: GeoJsonSource? = style.getSourceAs(Constants.ON_SEARCH_SOURCE_ID)
                source?.setGeoJson(FeatureCollection.fromFeatures(arrayOf(Feature.fromJson(selectedCarmenFeature.toJson()))))
                return LatLng((selectedCarmenFeature.geometry() as Point).latitude(),(selectedCarmenFeature.geometry() as Point).longitude())
            }
        }
        return null
    }
    fun addLayer(properties: Array<PropertyValue<*>>){
        style?.addLayer(SymbolLayer(Constants.ON_SEARCH_LAYER_ID, Constants.ON_SEARCH_SOURCE_ID).withProperties(*properties))
    }
    fun addSource(){
        style?.addSource(GeoJsonSource(Constants.ON_SEARCH_SOURCE_ID))
    }

    fun getLocationSearchIntent(): Intent {
        return PlaceAutocomplete.IntentBuilder()
            .accessToken(activity.getString(R.string.MapsToken))
            .placeOptions(buildPlaceOptions())
            .build(activity)
    }
    private fun buildPlaceOptions() =  PlaceOptions.builder()
        .backgroundColor(Color.parseColor("#EEEEEE"))
        .limit(10)
        .addInjectedFeature(home)
        .addInjectedFeature(work)
        .build(PlaceOptions.MODE_CARDS)

    private fun initializeUserLocations() {
        home = CarmenFeature.builder()
            .text("Mapbox SF Office")
            .geometry(Point.fromLngLat(-122.3964485, 37.7912561))
            .placeName("50 Beale St, San Francisco, CA")
            .id("mapbox-sf")
            .properties(JsonObject())
            .build()
        work = CarmenFeature.builder().text("Mapbox DC Office")
            .placeName("740 15th Street NW, Washington DC")
            .geometry(Point.fromLngLat(-77.0338348, 38.899750))
            .id("mapbox-dc")
            .properties(JsonObject())
            .build()
    }

}