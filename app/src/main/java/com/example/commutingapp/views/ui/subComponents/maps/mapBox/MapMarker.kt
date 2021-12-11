package com.example.commutingapp.views.ui.subComponents.maps.mapBox

import com.example.commutingapp.utils.others.Constants.ON_MAP_CLICK_LAYER_ID
import com.example.commutingapp.utils.others.Constants.ON_MAP_CLICK_SOURCE_ID
import com.example.commutingapp.views.ui.subComponents.maps.mapBox.layers.MapLayer
import com.example.commutingapp.views.ui.subComponents.maps.mapBox.layers.MapSymbolLayers
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style

class MapMarker( style: Style?) {
    private var latLng: LatLng? = null
    private var mapSymbol: MapLayer = MapSymbolLayers(style, ON_MAP_CLICK_SOURCE_ID, ON_MAP_CLICK_LAYER_ID)

      fun create(){
        val destinationLocation = latLng?.let {
            Point.fromLngLat(it.longitude,it.latitude)
        }
        val feature: Feature = Feature.fromGeometry(destinationLocation)
        mapSymbol.create(feature)

    }
    fun setLocation(latLng:LatLng){
        this.latLng = latLng
    }
    fun clear(){
        mapSymbol.clear()
    }

}