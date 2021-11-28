package com.example.commutingapp.views.ui.subComponents.maps.mapBox

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.example.commutingapp.R
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
import com.mapbox.mapboxsdk.plugins.traffic.TrafficPlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber


abstract class MapBox(private val view: View,private val activity: Activity):
    IMap<MapView> {

    private var mapBoxMap:MapboxMap? = null
    private var mapBoxView: MapView = view.findViewById(R.id.googleMapView)

    private var searchLocationButton: AppCompatButton  = view.findViewById(R.id.buttonLocationSearch)
    private lateinit var camera:MapCamera
    private lateinit var marker:MapMarker
    private lateinit var search:MapSearch
    private lateinit var location: MapLocationPuck
    private lateinit var directions: MapDirections
    private var mapTypes:MapTypes = MapTypes(activity)
    private var destinationLocation:LatLng? = null
    private var hasExistingMapMarker = false
    private var hasExistingMapRoute = false



    override fun deleteRouteAndMarkers(){
        CoroutineScope(Dispatchers.Main).launch {
            initializeStyles(mapTypes.loadMapType())
            hasExistingMapMarker = false
            hasExistingMapRoute = false
        }
    }

    override fun getMapView():MapView = mapBoxView


    override fun setupUI(mapType: String) {

        mapBoxView.getMapAsync { map ->
            CoroutineScope(Dispatchers.Main).launch {
                mapBoxMap = map
                initializeMap()
                initializeStyles(mapType)
                initializeSearchFABLocation()
                initializeMapComponents()

            }
    }
    }
    private suspend fun initializeMapComponents(){
            camera = MapCamera(mapBoxMap)
            location = MapLocationPuck(activity, mapBoxMap?.locationComponent)
            delay(50)
            mapBoxMap?.getStyle((location::buildLocationPuck))

    }
    private fun initializeMapSymbols(style: Style){
        marker = MapMarker(style)
        search = MapSearch(activity, style)
        directions = MapDirections(style, activity)
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeStyles(mapType:String) {

        CoroutineScope(Dispatchers.Main).launch {
            mapBoxMap?.setStyle(mapType) { style ->
                createMarkerImage(style)
                addTrafficView(style)
                initializeMapSymbols(style)
            }
        }
}

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun createMarkerImage(style:Style){
        activity.getDrawable(R.drawable.red_marker)?.let { style.addImage(MAP_MARKER_IMAGE_ID, it) }
    }

    private fun addTrafficView(style:Style){
        mapBoxView.apply {
            TrafficPlugin(this, mapBoxMap!!, style).apply { setVisibility(true) }
        }
    }

    @SuppressLint("BinaryOperationInTimber")
    override fun getLastKnownLocation(): LatLng? {
        mapBoxMap?.getStyle(location::buildLocationPuck)
        try {
            return mapBoxMap?.locationComponent?.lastKnownLocation?.run {
                LatLng(this.latitude, this.longitude)
            }
        } catch (e: Exception) {
            Timber.e("Getting last location failed " + e.message.toString())
        }
        return null
    }


    override fun updateMapStyle(style:String){

        mapBoxMap?.setStyle(style){
            CoroutineScope(Dispatchers.Main).launch {
            createMarkerImage(it)
            addTrafficView(it)
            initializeMapSymbols(it)
            destinationLocation?.let {
                createRouteDirection()
                createMapMarker(it)
            }


            }

        }

    }


    @SuppressLint("BinaryOperationInTimber")
    override fun getLocationSearchResult(requestCode: Int, resultCode: Int, data: Intent?) {
        deleteRouteAndMarkers()
        CoroutineScope(Dispatchers.Main).launch {
            delay(50)
            try {
                mapBoxMap?.getStyle {
                    search.getLocationSearchResult(requestCode, resultCode, data)?.let { location ->
                        camera.move(location, TRACKING_MAP_ZOOM, DEFAULT_CAMERA_ANIMATION_DURATION)
                        destinationLocation = location
                        hasExistingMapMarker = true
                    }
                }
            } catch (e: IllegalStateException) {
                Timber.e("Style loading error "+ e.message.toString())
            }
        }

    }



    @SuppressLint("BinaryOperationInTimber")
    override fun createLocationPuck() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                mapBoxMap?.getStyle {
                    location.buildLocationPuck(it)
                }
            } catch (e: IllegalArgumentException) {
                Timber.e("Location puck failed! "+ e.message.toString())
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
            hasExistingMapRoute = true
            createRouteDirection()
        }
    }
    private suspend fun createRouteDirection(){
        delay(50)
         mapBoxMap?.getStyle {
             if (hasExistingMapRoute) {
                 getLastKnownLocation()?.let { latLngLastLocation ->
                     destinationLocation?.let { latLngDestinationLocation ->
                         mutableListOf<Point>().apply {
                             this.add(
                                 Point.fromLngLat(
                                     latLngLastLocation.longitude,
                                     latLngLastLocation.latitude
                                 )
                             )
                             this.add(
                                 Point.fromLngLat(
                                     latLngDestinationLocation.longitude,
                                     latLngDestinationLocation.latitude
                                 )
                             )
                             directions.getRoute(this)

                         }
                     }
                 }
             }
         }

    }
    override fun pointMapMarker(latLng: LatLng) {
        deleteRouteAndMarkers()
        CoroutineScope(Dispatchers.Main).launch {
            hasExistingMapMarker = true
            createMapMarker(latLng)
        }
    }
    private suspend fun createMapMarker(location: LatLng){
        delay(50)
        mapBoxMap?.getStyle {
            if (hasExistingMapMarker) {
                mapBoxMap?.cameraPosition?.also { zoomLevel ->
                    camera.move(location, zoomLevel.zoom, FAST_CAMERA_ANIMATION_DURATION)
                }
                marker.setLocation(location)
                marker.create()
                destinationLocation = location
            }
        }
    }
    override fun moveCameraToUser(latLng: LatLng,zoomLevel:Double,cameraAnimationDuration:Int) {
        CoroutineScope(Dispatchers.Main).launch {
            camera.move(latLng, zoomLevel, cameraAnimationDuration)
        }
    }
}