package com.example.commutingapp.views.ui.subComponents.maps.mapBox


import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import com.example.commutingapp.BuildConfig
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.Constants
import com.example.commutingapp.utils.others.Constants.ROUTE_LAYER_ID
import com.example.commutingapp.utils.others.Constants.ROUTE_SOURCE_ID
import com.example.commutingapp.views.dialogs.CustomDialogBuilder
import com.example.commutingapp.views.dialogs.DialogDirector
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.optimization.v1.MapboxOptimization
import com.mapbox.api.optimization.v1.models.OptimizationResponse
import com.mapbox.core.constants.Constants.PRECISION_6
import com.mapbox.geojson.Geometry
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.rejowan.cutetoast.CuteToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class MapDirections(private val style: Style?, private val activity: Activity) {



    private var findRouteDialog: CustomDialogBuilder = DialogDirector(activity).buildFindRouteDialog()

    suspend fun getRoute(listOfCoordinates: MutableList<Point>) {
    findRouteDialog.show()
    withContext(Dispatchers.Default) {

        getClient(listOfCoordinates).enqueueCall(object : Callback<OptimizationResponse> {

            @SuppressLint("BinaryOperationInTimber")
            override fun onResponse(call: Call<OptimizationResponse>,response: Response<OptimizationResponse>) {

                response.body()?.trips()?.let {
                    if (it.isNotEmpty()) {
                        try {
                            drawNavigationRoute(it[0])
                        } catch (e: IllegalStateException) {
                            Timber.e("Map Directions " + e.message)
                        }
                    }
                    if (it.isEmpty()) {
                        CuteToast.ct(activity,activity.getString(R.string.unreachableDestination), CuteToast.LENGTH_LONG,CuteToast.WARN, true).show()
                    }
                    findRouteDialog.hide()
                }

            }

            override fun onFailure(call: Call<OptimizationResponse>, t: Throwable) {
                Timber.e("Routes: On Failure")
                findRouteDialog.hide()
            }

        })
    }

    }


    init {
        style?.apply {
            runBlocking {
                addSource(GeoJsonSource(ROUTE_SOURCE_ID))
                addLayerBelow(
                    LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID).withProperties(
                        PropertyFactory.lineColor(Color.parseColor(Constants.ROUTE_COLOR)),
                        PropertyFactory.lineWidth(Constants.ROUTE_WIDTH)
                    ), Constants.ON_MAP_CLICK_LAYER_ID
                )
            }
        }
    }


    fun clear() {
        style?.apply {
            removeLayer(ROUTE_LAYER_ID)
            removeSource(ROUTE_SOURCE_ID)

        }
    }



    private fun getClient(listOfCoordinates: MutableList<Point>) =
        MapboxOptimization.builder()
            .source(DirectionsCriteria.SOURCE_FIRST)
            .destination(DirectionsCriteria.SOURCE_ANY)
            .coordinates(listOfCoordinates)
            .accessToken(BuildConfig.MAPBOX_DOWNLOADS_TOKEN)
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .profile(DirectionsCriteria.PROFILE_DRIVING)
            .build()


    private fun createRoute(geometry: Geometry) {
        val source:GeoJsonSource? = style?.getSourceAs(ROUTE_SOURCE_ID)
        source?.let { it.setGeoJson(geometry) }
    }

    private fun drawNavigationRoute(route: DirectionsRoute) {

        style?.let {
            route.geometry()?.let {
                createRoute(LineString.fromPolyline(it, PRECISION_6))
            }
        }
    }


}

