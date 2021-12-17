package com.example.commutingapp.views.ui.subComponents.maps.mapBox

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.Constants.FAST_CAMERA_ANIMATION_DURATION
import com.example.commutingapp.utils.others.Constants.MAP_MARKER_IMAGE_ID
import com.example.commutingapp.utils.others.Constants.MAX_ZOOM_LEVEL_MAPS
import com.example.commutingapp.utils.others.Constants.MIN_ZOOM_LEVEL_MAPS
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
                refreshStyles()
                hasExistingMapMarker = false
                hasExistingMapRoute = false

        }
    }
    private fun refreshStyles(){
        initializeStyles(mapTypes.currentMapType())
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
    private fun initializeMapComponents(){
        if(!::camera.isInitialized){
            camera = MapCamera(mapBoxMap)
        }
        if(!::location.isInitialized) {
            location = MapLocationPuck(activity, mapBoxMap?.locationComponent)
        }
        createLocationPuck()
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
    abstract fun onMapTrafficInitialized(trafficPlugin: TrafficPlugin)
    abstract fun onMap3DBuildingInitialized(buildingPlugin: BuildingPlugin)
    abstract fun onMapReady(mapboxMap: MapboxMap)

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initializeStyles(mapType: String) {

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

                trafficPlugin = TrafficPlugin(this, mapBoxMap!!, style)
                onMapTrafficInitialized(trafficPlugin)

            if (!::building3DPlugin.isInitialized) {
                building3DPlugin = BuildingPlugin(this, mapBoxMap!!, style)
                building3DPlugin.setMinZoomLevel(15f)
                onMap3DBuildingInitialized(building3DPlugin)
            }
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun createMarkerImage(style:Style){
        activity.getDrawable(R.drawable.red_marker)?.let { style.addImage(MAP_MARKER_IMAGE_ID, it) }
    }


    @SuppressLint("BinaryOperationInTimber")
    override fun getLastKnownLocation(): LatLng? {
            try {
                createLocationPuck()
                return mapBoxMap?.locationComponent?.lastKnownLocation?.run {
                    LatLng(this.latitude, this.longitude)
                }
            } catch (e: Exception) {
                Timber.e("Getting last location failed " + e.message.toString())
            }
            return null
        }


    override fun updateMapStyle(style: String) {

        mapBoxMap?.setStyle(style) {style->
            if (style.isFullyLoaded) {
                createMarkerImage(style)
                initializePlugins(style)
                initializeMapSymbols(style)
                destinationLocation?.let { location->
                    createRouteDirection(location)
                    showUserLocation(location)
                }
            }
        }
    }

    override fun setVoiceSearchResult(place: String) {
/*
        runBlocking{ startGeocode(null,place) }
        destinationLocation?.let {
            hasExistingMapMarker = true
            showUserLocation(it)

        }

 */
    }

    @SuppressLint("BinaryOperationInTimber")
    override fun getLocationSearchResult(data: Intent?) {
        deleteRouteAndMarkers()
            try {
                mapBoxMap?.getStyle {
                    CoroutineScope(Dispatchers.Main).launch {
                    if(it.isFullyLoaded){
                        search.getLocationSearchResult(data).also { location ->
                            startGeocode(Point.fromLngLat(location.longitude,location.latitude),null)
                            destinationLocation = location
                            hasExistingMapMarker = true
                            showUserLocation(location)

                        }
                    }
                   }
                }
            } catch (e: IllegalStateException) {
                Timber.e("Style loading error "+ e.message.toString())
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
            destinationLocation?.let(::createRouteDirection)
    }
    private fun createRouteDirection(latLngDestinationLocation:LatLng){

         mapBoxMap?.getStyle {
             CoroutineScope(Dispatchers.Main).launch {
                 if (hasExistingMapRoute && it.isFullyLoaded) {
                     getLastKnownLocation()?.let { latLngLastLocation ->
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


    private suspend fun startGeocode(point: Point?, place: String?) {
        withContext(Dispatchers.Default) {
            try {
                buildGeocoding(point, place).enqueueCall(object : Callback<GeocodingResponse> {
                    override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
                        response.body()?.let {
                            val results = it.features()
                            if (results.size > 0) {
                                geocodeText.value = results[0].text()
                                geocodePlaceName.value = results[0].placeName()?.replace("${geocodeText.value}, ", "")
                            } else {
                                geocodeText.value = null
                                geocodePlaceName.value = null
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

    override fun pointMapMarker(location: LatLng) {
        CoroutineScope(Dispatchers.Main).launch {
            startGeocode(Point.fromLngLat(location.longitude, location.latitude),null)
            launch {
                deleteRouteAndMarkers()
            }.also {
                it.join()
            }.run {
                delay(50)
                if(isCompleted){
                    destinationLocation = location
                    hasExistingMapMarker = true
                    showUserLocation(location)
                }
            }

        }
    }

    private fun buildGeocoding(point: Point?, place:String?) =
        with(MapboxGeocoding.builder()){
            accessToken(activity.getString(R.string.MapsToken))
            point?.let(::query)
            place?.let(::query)
            build()
        }




    private fun showUserLocation(location: LatLng){

            mapBoxMap?.getStyle {
                    if (hasExistingMapMarker && it.isFullyLoaded) {
                        mapBoxMap?.cameraPosition?.also { zoomLevel ->
                            camera.move(location, zoomLevel.zoom, FAST_CAMERA_ANIMATION_DURATION) }
                        marker.setLocation(location)
                        marker.create()
                    }
                }
            }

    override fun moveCameraToUser(latLng: LatLng,zoomLevel:Double,cameraAnimationDuration:Int) {
            camera.move(latLng, zoomLevel, cameraAnimationDuration)
    }

    override fun clearCache() {
        if(this::directions.isInitialized) directions.clear()
        if(this::marker.isInitialized) marker.clear()

    }
}