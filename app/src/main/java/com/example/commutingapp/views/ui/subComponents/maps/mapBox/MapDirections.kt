package com.example.commutingapp.views.ui.subComponents.maps.mapBox

import android.app.Activity
import android.util.Log
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.Constants.ROUTE_LAYER_ID
import com.example.commutingapp.utils.others.Constants.ROUTE_SOURCE_ID
import com.example.commutingapp.views.ui.subComponents.maps.mapBox.layers.MapLayer
import com.example.commutingapp.views.ui.subComponents.maps.mapBox.layers.MapLineLayers
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.optimization.v1.MapboxOptimization
import com.mapbox.api.optimization.v1.models.OptimizationResponse
import com.mapbox.core.constants.Constants.PRECISION_6
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class MapDirections(private val style: Style?, private val activity: Activity) {


    private var mapLineLayers: MapLayer = MapLineLayers(style, ROUTE_SOURCE_ID, ROUTE_LAYER_ID)


      fun getRoute(locationOrigin:Point, destinationLocation:Point) {

             getClient(locationOrigin,destinationLocation).enqueueCall(object : Callback<DirectionsResponse> {

                 override fun onResponse(
                     call: Call<DirectionsResponse>,
                     response: Response<DirectionsResponse>
                 ) {

                     response.body()?.let { response ->
                         if(response.routes().size < 1){
                             Timber.e("No routes found!")
                             return;
                         }
                         CoroutineScope(Dispatchers.Main).launch { drawNavigationRoute(response.routes()[0]) }

                     }
                 }

                 override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                     Log.e("Routes:", "On Failure")
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


    private suspend fun drawNavigationRoute(route: DirectionsRoute) {

        style?.let {
            val map = HashMap<Int, Feature>()
            withContext(Dispatchers.Default) {
            val lineString = route.geometry()?.let { LineString.fromPolyline(it, PRECISION_6) }
            val coordinates: MutableList<Point> = lineString!!.coordinates()
                for (i in 0..coordinates.size) {
                    map[i] = Feature.fromGeometry(LineString.fromLngLats(coordinates))
                }
            }
            val directionsRouteFeatureList = ArrayList<Feature>(map.values)
            val dashedLineDirectionsFeatureCollection =
                FeatureCollection.fromFeatures(directionsRouteFeatureList)
            mapLineLayers.create(dashedLineDirectionsFeatureCollection)
        }
    }
}

