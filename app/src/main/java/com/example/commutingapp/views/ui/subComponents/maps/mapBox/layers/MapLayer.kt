package com.example.commutingapp.views.ui.subComponents.maps.mapBox.layers

import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.geometry.LatLng

interface MapLayer {
    fun create(featureCollection: FeatureCollection)
    fun create(feature: Feature)

}