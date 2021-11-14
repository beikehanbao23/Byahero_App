package com.example.commutingapp.views.ui.subComponents.maps.mapBox

import com.example.commutingapp.views.ui.subComponents.maps.mapBox.layers.MapSymbolLayers
import com.example.commutingapp.views.ui.subComponents.maps.mapBox.layers.properties.OnClickMapMarkerProperties
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style

class MapMarker(private val style: Style?) {
    var latLng: LatLng? = null
    private var mapSymbol: MapSymbolLayers = MapSymbolLayers(style,OnClickMapMarkerProperties())

     fun create(){
        val destinationLocation = latLng?.let {
            Point.fromLngLat(it.longitude,it.latitude)
        }
        val feature: Feature = Feature.fromGeometry(destinationLocation)
        mapSymbol.create(feature)

    }



}