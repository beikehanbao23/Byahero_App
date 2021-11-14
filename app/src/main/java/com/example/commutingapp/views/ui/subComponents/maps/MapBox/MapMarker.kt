package com.example.commutingapp.views.ui.subComponents.maps.MapBox

import com.example.commutingapp.utils.others.Constants
import com.example.commutingapp.utils.others.Constants.MAP_MARKER_IMAGE_ID
import com.example.commutingapp.utils.others.Constants.ON_MAP_CLICK_LAYER_ID
import com.example.commutingapp.utils.others.Constants.ON_MAP_CLICK_SOURCE_ID
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style

class MapMarker(private val style: Style?):MapLayer{
    var latLng: LatLng? = null
    private var mapSymbol:MapSymbolLayers = MapSymbolLayers(style, ON_MAP_CLICK_SOURCE_ID, ON_MAP_CLICK_LAYER_ID, MAP_MARKER_IMAGE_ID)

    override fun create(){
        val destinationLocation = latLng?.let {
            Point.fromLngLat(it.longitude,it.latitude)
        }
        val feature: Feature = Feature.fromGeometry(destinationLocation)
        mapSymbol.create(feature)

    }



}