package com.example.commutingapp.views.ui.subComponents.maps.mapBox.layers

import android.graphics.Color
import com.example.commutingapp.utils.others.Constants
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.GeoJson
import com.mapbox.geojson.Geometry
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.coroutines.*

class MapLineLayers(
    private val style: Style?,
    private val sourceId: String,
    private val layerId: String
) : MapLayer {

    private var source: GeoJsonSource? = null

    init {
        style?.apply {
              runBlocking {
                  addSource(GeoJsonSource(sourceId))
                  addLayerBelow(
                      LineLayer(layerId, sourceId).withProperties(
                          PropertyFactory.lineColor(Color.parseColor(Constants.ROUTE_COLOR)),
                          PropertyFactory.lineWidth(Constants.POLYLINE_WIDTH)
                      ), Constants.ON_MAP_CLICK_LAYER_ID
                  )
              }
          }
    }


    override fun create(feature: Feature) {

    }

    override fun create(geometry: Geometry) {
        source = style?.getSourceAs(sourceId)
        source?.let { it.setGeoJson(geometry) }
    }

    override  fun create(featureCollection: FeatureCollection): Unit {

        source = style?.getSourceAs(sourceId)
        source?.let { it.setGeoJson(featureCollection) }

    }



}
