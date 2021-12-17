package com.example.commutingapp.views.ui.subComponents.maps.mapBox

import com.example.commutingapp.utils.others.Constants
import com.example.commutingapp.utils.others.Constants.ON_MAP_CLICK_LAYER_ID
import com.example.commutingapp.utils.others.Constants.ON_MAP_CLICK_SOURCE_ID
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.coroutines.runBlocking

class MapMarker(private val style: Style?) {
    private var latLng: LatLng? = null
    init {
        style?.apply {
            runBlocking {
                addSource(GeoJsonSource(ON_MAP_CLICK_SOURCE_ID))
                addLayer(
                    SymbolLayer(ON_MAP_CLICK_LAYER_ID, ON_MAP_CLICK_SOURCE_ID).withProperties(
                        PropertyFactory.iconImage(Constants.MAP_MARKER_IMAGE_ID),
                        PropertyFactory.iconOffset(arrayOf(0f, -8f)),
                        PropertyFactory.iconSize(Constants.MAP_MARKER_SIZE)
                    )
                )
            }
        }
    }
      fun create(){
        val destinationLocation = latLng?.let {
            Point.fromLngLat(it.longitude,it.latitude) }
        val feature: Feature = Feature.fromGeometry(destinationLocation)
        createMarker(feature)

    }
    fun setLocation(latLng:LatLng){
        this.latLng = latLng
    }
    private fun createMarker(feature:Feature){
        val source:GeoJsonSource? = style?.getSourceAs(ON_MAP_CLICK_SOURCE_ID)
        source?.setGeoJson(feature)
    }
    fun clear(){
        style?.apply {
            removeSource(ON_MAP_CLICK_SOURCE_ID)
            removeLayer(ON_MAP_CLICK_LAYER_ID)
        }
    }

}