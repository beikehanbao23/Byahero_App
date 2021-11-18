package com.example.commutingapp.views.ui.subComponents.maps.mapBox

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.BitmapConvert
import com.example.commutingapp.utils.others.Constants
import com.example.commutingapp.utils.others.Constants.DEFAULT_CAMERA_ANIMATION_DURATION
import com.example.commutingapp.utils.others.Constants.FAST_CAMERA_ANIMATION_DURATION
import com.example.commutingapp.utils.others.Constants.MAP_MARKER_IMAGE_ID
import com.example.commutingapp.utils.others.Constants.MAX_ZOOM_LEVEL_MAPS
import com.example.commutingapp.utils.others.Constants.MIN_ZOOM_LEVEL_MAPS
import com.example.commutingapp.utils.others.Constants.TRACKING_MAP_ZOOM
import com.example.commutingapp.views.ui.subComponents.fab.MapTypes
import com.example.commutingapp.views.ui.subComponents.maps.IMap
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.LineManager
import com.mapbox.mapboxsdk.plugins.traffic.TrafficPlugin
import com.mapbox.mapboxsdk.utils.BitmapUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


abstract class MapBox(private val view: View,private val activity: Activity):
    IMap<MapboxMap, MapView> {

    private var mapBoxMap:MapboxMap? = null
    private var mapBoxView: MapView = view.findViewById(R.id.googleMapView)
    private var mapBoxStyle: Style? = null
    private var searchLocationButton: AppCompatButton  = view.findViewById(R.id.buttonLocationSearch)
    private lateinit var camera:MapCamera
    private lateinit var marker:MapMarker
    private lateinit var search:MapSearch
    private lateinit var locationPuck: MapLocationPuck
    private lateinit var polyLine: LineManager
    private lateinit var directions: MapDirections
    private var mapTypes:MapTypes = MapTypes(activity)
    private var destinationLocation:LatLng? = null


    override fun getMapInstance():MapboxMap? = mapBoxMap
    override fun deleteAllMapMarker(){
        CoroutineScope(Dispatchers.Main).launch {
            initializeStyles(mapTypes.loadMapType())
        }
    }

    override fun getMapViewInstance():MapView = mapBoxView


    override fun setupUI(mapType: String) {

        mapBoxView.getMapAsync { map ->
            CoroutineScope(Dispatchers.Main).launch {
                mapBoxMap = map
                initializeMap()
                initializeStyles(mapType)
                initializeSearchFABLocation()
                initializeMapMarkerImage()
                initializeMapComponents()
            }
    }
    }
    private suspend fun initializeMapComponents(){
            locationPuck = MapLocationPuck(activity, mapBoxMap)
            delay(10)
            mapBoxStyle?.let(locationPuck::buildLocationPuck)

    }
    private fun initializeMap() {

            with(mapBoxMap!!) {
                uiSettings.isAttributionEnabled = false
                uiSettings.isLogoEnabled = false
                uiSettings.setCompassMargins(0, 480, 50, 0)
                setMaxZoomPreference(MAX_ZOOM_LEVEL_MAPS)
                setMinZoomPreference(MIN_ZOOM_LEVEL_MAPS)
                onMapReady(this)
            }

    }

    abstract fun onMapReady(mapboxMap: MapboxMap)

    private  fun initializeStyles(mapType:String) {

            mapBoxMap?.setStyle(mapType) { style ->

                mapBoxStyle = style
                mapBoxView.apply {
                    TrafficPlugin(this, mapBoxMap!!, style).apply { setVisibility(true) }
                    polyLine = LineManager(this, mapBoxMap!!, style)
                }

                marker = MapMarker(style)
                search = MapSearch(activity, style)
                directions = MapDirections(style, activity)
        }
    }
    @SuppressLint("LogNotTimber")
    override fun getLastKnownLocation():LatLng?{
        try {
            return mapBoxMap?.locationComponent?.lastKnownLocation?.run {
                LatLng(this.latitude, this.longitude)
            } }catch (e:Exception){
            Log.e("Last known location",e.message.toString())
            mapBoxStyle?.let( locationPuck::buildLocationPuck)
            }
        return null
    }
    override fun recoverMissingMapMarker(){
            mapBoxView.addOnStyleImageMissingListener {
                CoroutineScope(Dispatchers.Main).launch {
                initializeMapMarkerImage()
            }
    }
    }

    override fun updateMapStyle(style:String){
        mapBoxMap?.setStyle(style){ mapStyle -> mapBoxStyle = mapStyle}
    }


    override fun getLocationSearchResult(requestCode: Int, resultCode: Int, data: Intent?) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                mapBoxMap?.getStyle {
                    search.getLocationSearchResult(requestCode, resultCode, data)?.let { location ->
                        moveCameraToUser(
                            location,
                            TRACKING_MAP_ZOOM,
                            DEFAULT_CAMERA_ANIMATION_DURATION
                        )
                    }
                }
            } catch (e: IllegalStateException) {
                Log.e("Style loading error ", e.message.toString())
            }
        }

    }

    private  fun initializeMapMarkerImage()=
        activity.getDrawable(R.drawable.red_marker)
            ?.let { mapBoxStyle?.addImage(MAP_MARKER_IMAGE_ID, it) }


    override fun initializeLocationPuck() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                mapBoxStyle?.let( locationPuck::buildLocationPuck)
            } catch (e: IllegalArgumentException) {
                Log.e("Location puck failed! ", e.message.toString())
            }
        }

    }

    private fun initializeSearchFABLocation(){
            searchLocationButton.setOnClickListener {
                onSearchCompleted(search.getLocationSearchIntent())
        }

    }
    abstract fun onSearchCompleted(intent:Intent)

    override fun createDirections() {
        CoroutineScope(Dispatchers.Main).launch {
            getLastKnownLocation()?.let { latLngLastLocation ->
                destinationLocation?.let { latLngDestinationLocation ->
                    val origin: Point =
                        Point.fromLngLat(latLngLastLocation.longitude, latLngLastLocation.latitude)
                    val destination = Point.fromLngLat(
                        latLngDestinationLocation.longitude,
                        latLngDestinationLocation.latitude
                    )

                    directions.getRoute(origin,destination)
                }
            }
        }
    }

    override fun pointMapMarker(latLng: LatLng) {
        CoroutineScope(Dispatchers.Main).launch {
            mapBoxMap?.getStyle {

                marker.setLocation(latLng)
                marker.create()
                destinationLocation = latLng

                mapBoxMap?.cameraPosition?.also { zoomLevel ->
                    moveCameraToUser(latLng, zoomLevel.zoom, FAST_CAMERA_ANIMATION_DURATION)
                }
            }
        }
    }
    override fun moveCameraToUser(latLng: LatLng,zoomLevel:Double,cameraAnimationDuration:Int) {
        CoroutineScope(Dispatchers.Main).launch {
            camera = MapCamera(mapBoxMap)
            camera.move(latLng, zoomLevel, cameraAnimationDuration)
        }
    }
}