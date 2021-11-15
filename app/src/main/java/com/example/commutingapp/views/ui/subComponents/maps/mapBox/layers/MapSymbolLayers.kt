package com.example.commutingapp.views.ui.subComponents.maps.mapBox.layers

import com.example.commutingapp.utils.others.Constants

import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection

import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory

import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.style.layers.SymbolLayer

class MapSymbolLayers(private val style: Style?, private val sourceId:String, private val layerId:String ):MapLayer {

    private var source: GeoJsonSource? = null

    init{
        style?.apply {
            addSource(GeoJsonSource(sourceId))
            addLayer(SymbolLayer(layerId, sourceId).withProperties(
                PropertyFactory.iconImage(Constants.MAP_MARKER_IMAGE_ID),
                PropertyFactory.iconOffset(arrayOf(0f, -8f)),
                PropertyFactory.iconSize(Constants.MAP_MARKER_SIZE)
            ))
        }
    }

    override fun create(feature:Feature){
        source = style?.getSourceAs(sourceId)
        source?.let { it.setGeoJson(feature) }
    }
    override fun create(featureCollection: FeatureCollection){
        source = style?.getSourceAs(sourceId)
        source?.let { it.setGeoJson(featureCollection) }
    }

}

