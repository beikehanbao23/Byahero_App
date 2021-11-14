package com.example.commutingapp.views.ui.subComponents.maps.mapBox

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.BitmapConvert
import com.example.commutingapp.utils.others.Constants.DEFAULT_CAMERA_ANIMATION_DURATION
import com.example.commutingapp.utils.others.Constants.FAST_CAMERA_ANIMATION_DURATION
import com.example.commutingapp.utils.others.Constants.MAP_MARKER_IMAGE_ID
import com.example.commutingapp.utils.others.Constants.MAX_ZOOM_LEVEL_MAPS
import com.example.commutingapp.utils.others.Constants.MIN_ZOOM_LEVEL_MAPS
import com.example.commutingapp.utils.others.Constants.TRACKING_MAP_ZOOM
import com.example.commutingapp.views.ui.subComponents.fab.MapTypes
import com.example.commutingapp.views.ui.subComponents.maps.IMap
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.LineManager
import com.mapbox.mapboxsdk.plugins.traffic.TrafficPlugin
import timber.log.Timber
import java.lang.IllegalStateException

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
    private var mapTypes:MapTypes = MapTypes(activity)



    override fun getMapInstance():MapboxMap? = mapBoxMap
    override fun deleteAllMapMarker(){
       initializeStyles(mapTypes.loadMapType())
    }

    override fun getMapViewInstance():MapView = mapBoxView


    override fun setupUI(mapType: String){
        mapBoxView.getMapAsync {map->
            mapBoxMap = map
                initializeMap()
                initializeStyles(mapType)
                initializeSearchFABLocation()
                initializeSearchFABLocation()
                initializeMapMarkerImage()
                initializeMapComponents()

        }
    }
    private fun initializeMapComponents(){

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
            setMaxZoomPreference(MAX_ZOOM_LEVEL_MAPS)
            setMinZoomPreference(MIN_ZOOM_LEVEL_MAPS)
            onMapReady(this)
        }
    }

    abstract fun onMapReady(mapboxMap: MapboxMap)

    private fun initializeStyles(mapType:String){
        mapBoxMap?.setStyle(mapType) { style ->
            mapBoxStyle = style
            mapBoxView.apply {
                TrafficPlugin(this, mapBoxMap!!, style).apply { setVisibility(true) }
                polyLine = LineManager(this,mapBoxMap!!,style)
            }
                marker = MapMarker(style)
                search = MapSearch(activity, style)

        }
    }

    override fun getLastKnownLocation():LatLng?{
        try {
            return mapBoxMap?.locationComponent?.lastKnownLocation?.run {
                LatLng(this.latitude, this.longitude)
            } }catch (e:Exception){}
        return null
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
    try {
        search.getLocationSearchResult(requestCode, resultCode, data)?.let { location ->
            moveCameraToUser(location, TRACKING_MAP_ZOOM, DEFAULT_CAMERA_ANIMATION_DURATION)
        }}catch(e:IllegalStateException){ Timber.d("Loading style error")}
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

    override fun pointMapMarker(latLng: LatLng) {
        mapBoxMap?.getStyle {
            marker.setLocation(latLng)
            marker.create()
        }
        mapBoxMap?.cameraPosition?.also {zoomLevel->
            moveCameraToUser(latLng, zoomLevel.zoom, FAST_CAMERA_ANIMATION_DURATION)
        }

    }
    override fun moveCameraToUser(latLng: LatLng,zoomLevel:Double,cameraAnimationDuration:Int) {
        camera = MapCamera(mapBoxMap)
        camera.move(latLng, zoomLevel, cameraAnimationDuration)
    }

}