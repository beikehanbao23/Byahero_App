package com.example.commutingapp.views.ui.subComponents.maps.mapBox

import android.app.Activity
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.Constants.ROUTE_LAYER_ID
import com.example.commutingapp.utils.others.Constants.ROUTE_SOURCE_ID
import com.example.commutingapp.views.ui.subComponents.maps.mapBox.layers.MapLayer
import com.example.commutingapp.views.ui.subComponents.maps.mapBox.layers.MapLineLayers
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.geojson.Point
import retrofit2.Callback;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import retrofit2.Call
import retrofit2.Response
import com.mapbox.core.constants.Constants.PRECISION_6
import com.mapbox.geojson.FeatureCollection
import timber.log.Timber


class MapDirections(private val style:Style?, private val activity: Activity) {


    private var mapLineLayers:MapLayer = MapLineLayers(style,ROUTE_SOURCE_ID,ROUTE_LAYER_ID)


    fun getRoute(locationOrigin:Point, destinationLocation:Point){
        getClient(locationOrigin, destinationLocation).enqueueCall(object: Callback<DirectionsResponse> {
            override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                response.body()?.let {response->

                    if(response.routes().size < 1){
                        Timber.d("No routes found!")
                        return;
                    }
                    drawNavigationRoute(response.routes()[0])
                }
            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                Timber.d("Error: %s", t.message);
            }
        })
    }

    private fun getClient(locationOrigin: Point,destinationLocation: Point) = MapboxDirections.builder()
        .origin(locationOrigin)
        .destination(destinationLocation)
        .overview(DirectionsCriteria.OVERVIEW_FULL)
        .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
        .accessToken(activity.getString(R.string.MapsToken))
        .build()

    private fun drawNavigationRoute(route:DirectionsRoute){


        style?.let {
                val directionsRouteFeatureList = mutableListOf<Feature>()

                val lineString = route.geometry()?.let {LineString.fromPolyline(it, PRECISION_6) }
                val coordinates:MutableList<Point> = lineString!!.coordinates()
                    for (i in 0..coordinates.size){
                        directionsRouteFeatureList.add(Feature.fromGeometry(LineString.fromLngLats(coordinates)))
                    }
                    val dashedLineDirectionsFeatureCollection = FeatureCollection.fromFeatures(directionsRouteFeatureList);
                    mapLineLayers.create(dashedLineDirectionsFeatureCollection)


        }
    }
}