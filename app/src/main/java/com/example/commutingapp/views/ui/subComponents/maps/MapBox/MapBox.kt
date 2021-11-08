package com.example.commutingapp.views.ui.subComponents.maps.MapBox

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.example.commutingapp.BuildConfig
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.BitmapConvert
import com.example.commutingapp.utils.others.Constants
import com.example.commutingapp.utils.others.Constants.DEFAULT_CAMERA_ANIMATION_DURATION
import com.example.commutingapp.utils.others.Constants.FAST_CAMERA_ANIMATION_DURATION
import com.example.commutingapp.utils.others.Constants.ON_SEARCH_SOURCE_ID

import com.example.commutingapp.utils.others.Constants.MAP_MARKER_IMAGE_ID
import com.example.commutingapp.utils.others.Constants.MINIMUM_MAP_LEVEL
import com.example.commutingapp.utils.others.Constants.ON_SEARCH_LAYER_ID
import com.example.commutingapp.utils.others.Constants.TRACKING_MAP_ZOOM
import com.example.commutingapp.views.ui.subComponents.maps.IMap
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng

import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style

import com.mapbox.mapboxsdk.plugins.traffic.TrafficPlugin
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.PropertyValue

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


    override fun getMapInstance():MapboxMap? = mapBoxMap
    override fun deleteAllMapMarker(){
        initializeType(BuildConfig.MAP_STYLE)

    }

    override fun getMapViewInstance():MapView = mapBoxView




    override fun setupUI(mapType: String){
        mapBoxView.getMapAsync {map->
            mapBoxMap = map
                initializeMap()
                initializeType(mapType)
                initializeSearchFABLocation()
                initializeSearchFABLocation()
                initializeMapMarkerImage()
                initializeMapComponents(mapBoxMap!!)
        }
    }
    private fun initializeMapComponents(mapboxMap: MapboxMap){
        camera = MapCamera(mapboxMap)
        locationPuck = MapLocationPuck(activity,mapBoxMap)

        Handler(Looper.getMainLooper()).postDelayed({
            mapBoxStyle?.let(locationPuck::buildLocationPuck)
        }, 10)




    }
    private fun initializeMap() {
        with(mapBoxMap!!) {
            uiSettings.isAttributionEnabled = false
            uiSettings.isLogoEnabled = false
            uiSettings.setCompassMargins(0, 480, 50, 0)
            setMaxZoomPreference(MINIMUM_MAP_LEVEL)
            onMapReady(this)
        }
    }

    abstract fun onMapReady(mapboxMap: MapboxMap)

    private fun initializeType(mapType:String){
        mapBoxMap?.setStyle(mapType) { style ->
            mapBoxStyle = style
            mapBoxView.apply {
                TrafficPlugin(this, mapBoxMap!!, style).apply { setVisibility(true) }
            }

                marker = MapMarker(style)
                search = MapSearch(activity, style)
                addSource()
                addLayer()

        }
    }

    override fun getLastKnownLocation():LatLng? = mapBoxMap?.locationComponent?.lastKnownLocation?.run {
        LatLng(this.latitude, this.longitude)
    }
    override fun recoverMissingMapMarker(){
        mapBoxView.addOnStyleImageMissingListener {
            initializeMapMarkerImage()
        }
    }

    override fun updateMapStyle(style:String){
        mapBoxMap?.setStyle(style){ mapStyle -> mapBoxStyle = mapStyle}
    }

    override fun getLocationSearchResult(requestCode: Int,resultCode: Int, data: Intent?) {

        search.getLocationSearchResult(requestCode, resultCode, data)?.let {location->
            moveCameraToUser(location, TRACKING_MAP_ZOOM, DEFAULT_CAMERA_ANIMATION_DURATION)
        }
    }

    private fun initializeMapMarkerImage(){
        mapBoxStyle?.addImage(
            MAP_MARKER_IMAGE_ID,
            BitmapConvert.getBitmapFromVectorDrawable(
                activity,
                R.drawable.ic_location_map_marker
            )
        )
    }
    override fun initializeLocationPuck() {
        try {
            mapBoxStyle?.let(locationPuck::buildLocationPuck)
        }catch (e:IllegalArgumentException){}
    }


    private fun initializeSearchFABLocation(){
        searchLocationButton.setOnClickListener {
                onSearchCompleted(search.getLocationSearchIntent())
        }

    }
    abstract fun onSearchCompleted(intent:Intent)
    private fun addSource() {
        search.addSource()
        marker.addSource()
    }
    private fun addLayer() {


            search.addLayer(symbolLayerProperties())
            marker.addLayer(symbolLayerProperties())

    }
    private fun symbolLayerProperties()= arrayOf<PropertyValue<*>>(
        PropertyFactory.iconImage(MAP_MARKER_IMAGE_ID),
        PropertyFactory.iconOffset(arrayOf(0f, -8f)),
        PropertyFactory.iconSize(Constants.MAP_MARKER_SIZE)
    )
    override fun pointMapMarker(latLng: LatLng) {
        mapBoxMap?.getStyle {
            marker.createMarker(latLng)
        }
        mapBoxMap?.cameraPosition?.also {zoomLevel->
            moveCameraToUser(latLng, zoomLevel.zoom, FAST_CAMERA_ANIMATION_DURATION)
        }

    }
    override fun moveCameraToUser(latLng: LatLng,zoomLevel:Double,cameraAnimationDuration:Int) {
        camera.move(latLng, zoomLevel, cameraAnimationDuration)
    }

}