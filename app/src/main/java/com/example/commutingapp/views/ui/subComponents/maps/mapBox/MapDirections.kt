package com.example.commutingapp.views.ui.subComponents.maps.mapBox


import android.annotation.SuppressLint
import android.app.Activity
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.Constants.ROUTE_LAYER_ID
import com.example.commutingapp.utils.others.Constants.ROUTE_SOURCE_ID
import com.example.commutingapp.views.ui.subComponents.maps.mapBox.layers.MapLayer
import com.example.commutingapp.views.ui.subComponents.maps.mapBox.layers.MapLineLayers
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.optimization.v1.MapboxOptimization
import com.mapbox.api.optimization.v1.models.OptimizationResponse
import com.mapbox.core.constants.Constants.PRECISION_6
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.maps.Style
import com.rejowan.cutetoast.CuteToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class MapDirections(private val style: Style?, private val activity: Activity) {


    private var mapLineLayers: MapLayer = MapLineLayers(style, ROUTE_SOURCE_ID, ROUTE_LAYER_ID)


    fun getRoute(listOfCoordinates: MutableList<Point>) {

        getClient(listOfCoordinates).enqueueCall(object : Callback<OptimizationResponse> {

            @SuppressLint("BinaryOperationInTimber")
            override fun onResponse(call: Call<OptimizationResponse>, response: Response<OptimizationResponse>) {

                response.body()?.trips()?.let {
                    if (it.isNotEmpty()) {
                        try {
                           drawNavigationRoute(it[0])
                        } catch (e: IllegalStateException) {
                            Timber.e("Map Directions " + e.message)
                        }
                    }
                    if(it.isEmpty()){
                        CuteToast.ct(activity,activity.getString(R.string.unreachableDestination), CuteToast.LENGTH_LONG, CuteToast.WARN, true).show();
                    }
                }
            }

            override fun onFailure(call: Call<OptimizationResponse>, t: Throwable) {
                Timber.e("Routes: On Failure")
            }
        })

    }


    private fun getClient(listOfCoordinates: MutableList<Point>) =
        MapboxOptimization.builder()
            .source(DirectionsCriteria.SOURCE_FIRST)
            .destination(DirectionsCriteria.SOURCE_ANY)
            .coordinates(listOfCoordinates)
            .accessToken(activity.getString(R.string.MapsToken))
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .profile(DirectionsCriteria.PROFILE_DRIVING)
            .build()


    private fun drawNavigationRoute(route: DirectionsRoute) {

        style?.let {
            route.geometry()?.let {
                mapLineLayers.create(LineString.fromPolyline(it, PRECISION_6))
            }
        }
    }

    fun clear(){
        mapLineLayers.clear()
    }
}

