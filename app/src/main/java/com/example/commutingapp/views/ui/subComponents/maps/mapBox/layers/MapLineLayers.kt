package com.example.commutingapp.views.ui.subComponents.maps.mapBox.layers

import android.graphics.Color
import com.example.commutingapp.utils.others.Constants

import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

class MapLineLayers(private val style: Style?, private val sourceId:String, private val layerId:String):MapLayer {

    private var source:GeoJsonSource? = null

    init {
        style?.apply {
            addSource(GeoJsonSource(sourceId))

                addLayerBelow(
                    LineLayer(layerId, sourceId).withProperties(
                        PropertyFactory.lineColor(Color.CYAN),
                        PropertyFactory.lineWidth(Constants.POLYLINE_WIDTH)
                    ),Constants.ON_MAP_CLICK_LAYER_ID)

        }
    }


        override fun create(featureCollection: FeatureCollection) {
            source = style?.getSourceAs(sourceId)
            source?.let { it.setGeoJson(featureCollection) }
        }

        override fun create(feature: Feature){}

    }
