package com.example.commutingapp.views.ui.subComponents.maps.mapBox

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.Constants.DEFAULT_CAMERA_ANIMATION_DURATION
import com.example.commutingapp.utils.others.Constants.FAST_CAMERA_ANIMATION_DURATION
import com.example.commutingapp.utils.others.Constants.MAP_MARKER_IMAGE_ID
import com.example.commutingapp.utils.others.Constants.MAX_ZOOM_LEVEL_MAPS
import com.example.commutingapp.utils.others.Constants.MIN_ZOOM_LEVEL_MAPS
import com.example.commutingapp.utils.others.Constants.TRACKING_MAP_ZOOM
import com.example.commutingapp.views.ui.subComponents.fab.MapTypes
import com.example.commutingapp.views.ui.subComponents.maps.IMap
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.core.exceptions.ServicesException
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin
import com.mapbox.mapboxsdk.plugins.traffic.TrafficPlugin
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    private var geocodePlaceName = MutableLiveData<String?>()
    private var geocodeText = MutableLiveData<String?>()
    private lateinit var trafficPlugin: TrafficPlugin
    private lateinit var building3DPlugin : BuildingPlugin


    override fun deleteRouteAndMarkers(){
        if(hasExistingMapMarker) {
                initializeStyles(mapTypes.currentMapType())
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
        if(!::camera.isInitialized){
            camera = MapCamera(mapBoxMap)
        }
        if(!::location.isInitialized) {
            location = MapLocationPuck(activity, mapBoxMap?.locationComponent)
        }
            delay(50)
            mapBoxMap?.getStyle{ runBlocking { location.buildLocationPuck(it) }}
    }
    private fun initializeMapSymbols(style: Style) {

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

            mapBoxMap?.setStyle(mapType) { style ->
                    if (style.isFullyLoaded) {
                        createMarkerImage(style)
                        initializePlugins(style)
                        initializeMapSymbols(style)
                    }
        }
    }
    private fun initializePlugins(style: Style){
        mapBoxView.apply {
            if(!::trafficPlugin.isInitialized){
                trafficPlugin = TrafficPlugin(this, mapBoxMap!!, style)
            }
            if (!::building3DPlugin.isInitialized) {
                building3DPlugin = BuildingPlugin(this, mapBoxMap!!, style)
                building3DPlugin.setMinZoomLevel(15f)
            }
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun createMarkerImage(style:Style){
        activity.getDrawable(R.drawable.red_marker)?.let { style.addImage(MAP_MARKER_IMAGE_ID, it) }
    }



    override fun showTrafficView() {
        if(trafficPlugin.isVisible) trafficPlugin.setVisibility(false) else trafficPlugin.setVisibility(true)

    }
    override fun show3DBuildingView() {
         if(building3DPlugin.isVisible) building3DPlugin.setVisibility(false) else building3DPlugin.setVisibility(true)
    }


    @SuppressLint("BinaryOperationInTimber")
    override fun getLastKnownLocation(): LatLng? {
            try {
                mapBoxMap?.getStyle {
                runBlocking {location.buildLocationPuck(it)} }
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
                if(it.isFullyLoaded) {
                    createMarkerImage(it)
                    initializePlugins(it)
                    initializeMapSymbols(it)
                    destinationLocation?.let {
                        createRouteDirection()
                        createMapMarker(it)
                    }
                }
            }
        }
    }


    @SuppressLint("BinaryOperationInTimber")
    override fun getLocationSearchResult(requestCode: Int, resultCode: Int, data: Intent?) {
        deleteRouteAndMarkers()
            try {
                mapBoxMap?.getStyle {
                    CoroutineScope(Dispatchers.Main).launch {
                    if(it.isFullyLoaded){
                    search.getLocationSearchResult(requestCode, resultCode, data)?.let { location ->
                        camera.move(location, TRACKING_MAP_ZOOM, DEFAULT_CAMERA_ANIMATION_DURATION)
                        destinationLocation = location
                        hasExistingMapMarker = true
                        reverseGeocode(Point.fromLngLat(location.longitude,location.latitude))}
                    }
                   }
                }
            } catch (e: IllegalStateException) {
                Timber.e("Style loading error "+ e.message.toString())
            //TODO(FIX STYLE NOT LOADED)
            }

    }



    @SuppressLint("BinaryOperationInTimber")
    override fun createLocationPuck() {
            try {
                mapBoxMap?.getStyle {
                    runBlocking { location.buildLocationPuck(it) }
                }
            } catch (e: IllegalArgumentException) {
                Timber.e("Location puck failed! "+ e.message.toString())
            }
        }



    private fun initializeSearchFABLocation(){
            searchLocationButton.setOnClickListener {
                onSearchCompleted(search.getLocationSearchIntent())
        }

    }
    abstract fun onSearchCompleted(intent:Intent)

    override fun createDirections() {
            hasExistingMapRoute = true
            createRouteDirection()
    }
    private fun createRouteDirection(){

         mapBoxMap?.getStyle {
             CoroutineScope(Dispatchers.Main).launch {
                 if (hasExistingMapRoute && it.isFullyLoaded) {
                     getLastKnownLocation()?.let { latLngLastLocation ->
                         destinationLocation?.let { latLngDestinationLocation ->
                             mutableListOf<Point>().apply {
                                 this.add(Point.fromLngLat(latLngLastLocation.longitude, latLngLastLocation.latitude))
                                 this.add(Point.fromLngLat(latLngDestinationLocation.longitude,latLngDestinationLocation.latitude))
                                 directions.getRoute(this)
                             }
                         }
                     }
                 }
             }
         }
    }


    private suspend fun reverseGeocode(point: Point) {
        withContext(Dispatchers.Default) {
            try {
                buildGeocoding(point).enqueueCall(object : Callback<GeocodingResponse> {
                    override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
                        response.body()?.let {
                            val results = it.features()
                            if (results.size > 0) {
                                geocodeText.value = results[0].text() ?: "----"
                                geocodePlaceName.value = results[0].placeName()?.replace("${geocodeText.value}, ", "") ?: "----"
                            }
                        }
                    }

                    override fun onFailure(call: Call<GeocodingResponse>, throwable: Throwable) {
                        Timber.e("Geocoding Failure")
                    }
                })
            } catch (servicesException: ServicesException) {
                Timber.e(servicesException.toString())
            }
        }
    }
    override fun getPlaceText(): LiveData<String?> = geocodeText
    override fun getPlaceName():LiveData<String?> = geocodePlaceName

    override fun pointMapMarker(latLng: LatLng) {
        CoroutineScope(Dispatchers.Main).launch {
            val job = launch {  deleteRouteAndMarkers() }.also { it.join() }
            delay(50)
            if(job.isCompleted){
                hasExistingMapMarker = true
                createMapMarker(latLng)
            }
            reverseGeocode(Point.fromLngLat(latLng.longitude, latLng.latitude))
        }
    }


    private fun buildGeocoding(point: Point)=MapboxGeocoding.builder()
        .accessToken(activity.getString(R.string.MapsToken))
        .query(point)
        .build()


    private fun createMapMarker(location: LatLng){

            mapBoxMap?.getStyle {
                    if (hasExistingMapMarker && it.isFullyLoaded) {
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
            camera.move(latLng, zoomLevel, cameraAnimationDuration)
    }

    override fun clearCache() {
        if(this::directions.isInitialized) directions.clear()
        if(this::marker.isInitialized) marker.clear()
        if(this::search.isInitialized) search.clear()

    }
}