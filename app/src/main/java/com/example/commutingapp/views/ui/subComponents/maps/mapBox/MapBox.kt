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
import kotlin.reflect.KFunction2

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
                    showUserLocation(location, null)
                }
            }
        }
    }

    override fun setVoiceSearchResult(place: String) {
        hasExistingMapMarker = true
        CoroutineScope(Dispatchers.Main).launch {
            startGeocoding(null,place,::showUserLocation)
        }

    }

    @SuppressLint("BinaryOperationInTimber")
    override fun locationSearchResult(data: Intent?) {
        deleteRouteAndMarkers()
        try {
            mapBoxMap?.getStyle {
                CoroutineScope(Dispatchers.Main).launch {
                    if(it.isFullyLoaded){
                        search.getLocationSearchResult(data).also { location ->
                            startGeocoding(Point.fromLngLat(location.longitude,location.latitude),null,null)
                            destinationLocation = location
                            hasExistingMapMarker = true
                            showUserLocation(location, TRACKING_MAP_ZOOM)

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
                CoroutineScope(Dispatchers.Main).launch { location.buildLocationPuck(it) }
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


    private suspend fun startGeocoding(point: Point?, place: String?, showUserLocationUsingSearch: KFunction2<LatLng, Double?, Unit>?) {
        withContext(Dispatchers.Default) {
            try {
                buildGeocoding(point, place).enqueueCall(geocodingCallback(place,showUserLocationUsingSearch))
            } catch (servicesException: ServicesException) {
                Timber.e(servicesException.toString())
            }
        }
    }
    private fun geocodingCallback( place: String?, showUserLocationUsingSearch: KFunction2<LatLng, Double?, Unit>?) = object: Callback<GeocodingResponse>{
        override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
            geocodingResponse(response, place, showUserLocationUsingSearch)
        }

        override fun onFailure(call: Call<GeocodingResponse>, throwable: Throwable) {
            Timber.e("Geocoding Failure")
        }
    }
    private fun geocodingResponse(response: Response<GeocodingResponse>, place:String?, showUserLocationUsingSearch: KFunction2<LatLng, Double?, Unit>?){
        response.body()?.let {
            val results = it.features()
            if (results.size > 0) {
                val data = results[0]
                geocodeText.value = data.text()
                geocodePlaceName.value = data.placeName()?.replace("${geocodeText.value}, ", "")
                if (showUserLocationUsingSearch != null && place != null) {
                    destinationLocation = LatLng(data.center()!!.latitude() , data.center()!!.longitude())
                    showUserLocationUsingSearch(destinationLocation!!,TRACKING_MAP_ZOOM)
                }
            } else {
                geocodeText.value = null
                geocodePlaceName.value = null
            }
        }
    }
    override fun getPlaceText(): LiveData<String?> = geocodeText
    override fun getPlaceName():LiveData<String?> = geocodePlaceName

    override fun pointMapMarker(latLng: LatLng) {
        CoroutineScope(Dispatchers.Main).launch {
            startGeocoding(Point.fromLngLat(latLng.longitude, latLng.latitude),null,null)
            launch {
                deleteRouteAndMarkers()
            }.also {
                it.join()
            }.run {
                delay(50)
                if(isCompleted){
                    destinationLocation = latLng
                    hasExistingMapMarker = true
                    showUserLocation(latLng,TRACKING_MAP_ZOOM)
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




    private fun showUserLocation(location: LatLng, zoom: Double?){

        mapBoxMap?.getStyle { style->
            if (hasExistingMapMarker && style.isFullyLoaded) {
                mapBoxMap?.cameraPosition?.also {
                    camera.move(
                        location,
                        zoom ?: it.zoom,
                        DEFAULT_CAMERA_ANIMATION_DURATION) }
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