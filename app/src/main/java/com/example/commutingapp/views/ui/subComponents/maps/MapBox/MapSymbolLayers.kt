package com.example.commutingapp.views.ui.subComponents.maps.MapBox

import com.example.commutingapp.utils.others.Constants
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection

import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory

import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.style.layers.SymbolLayer

class MapSymbolLayers(private val style: Style?, private val sourceId:String, private val layerId:String, private val imageId:String) {

    private var source: GeoJsonSource? = null

    init{
        style?.apply {
            addSource(GeoJsonSource(sourceId))
            addLayer(SymbolLayer(layerId, sourceId).withProperties(
                PropertyFactory.iconImage(imageId),
                PropertyFactory.iconOffset(arrayOf(0f, -8f)),
                PropertyFactory.iconSize(Constants.MAP_MARKER_SIZE)
            ))
        }
    }

    fun create(feature:Feature){
        source = style?.getSourceAs(sourceId)
        source?.let { it.setGeoJson(feature) }
    }
    fun create(featureCollection: FeatureCollection){
        source = style?.getSourceAs(sourceId)
        source?.let { it.setGeoJson(featureCollection) }
    }

}

