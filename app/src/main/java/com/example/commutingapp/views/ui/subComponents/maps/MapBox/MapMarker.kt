package com.example.commutingapp.views.ui.subComponents.maps.MapBox

import com.example.commutingapp.utils.others.Constants
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyValue
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

class MapMarker(private val style: Style?) {

    fun createMarker(latLng:LatLng){
        val destinationLocation = Point.fromLngLat(latLng.longitude,latLng.latitude)
        val source: GeoJsonSource? = style?.getSourceAs(Constants.ON_MAP_CLICK_SOURCE_ID)
        val feature: Feature = Feature.fromGeometry(destinationLocation)
        source?.let {
            it.setGeoJson(feature)
        }
    }
    fun addLayer(properties: Array<PropertyValue<*>>){
        style?.addLayer(
            SymbolLayer(
                Constants.ON_MAP_CLICK_LAYER_ID,
                Constants.ON_MAP_CLICK_SOURCE_ID
            ).withProperties(*properties))
    }
    fun addSource(){
        style?.addSource(GeoJsonSource(Constants.ON_MAP_CLICK_SOURCE_ID))
    }

}