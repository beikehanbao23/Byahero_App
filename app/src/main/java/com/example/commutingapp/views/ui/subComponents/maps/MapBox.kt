package com.example.commutingapp.views.ui.subComponents.maps

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.example.commutingapp.BuildConfig
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.BitmapConvert
import com.example.commutingapp.utils.others.Constants
import com.example.commutingapp.utils.others.Constants.DEFAULT_CAMERA_ANIMATION_DURATION
import com.example.commutingapp.utils.others.Constants.GEO_JSON_SOURCE_LAYER_ID
import com.example.commutingapp.utils.others.Constants.ICON_LAYER_ID
import com.example.commutingapp.utils.others.Constants.ICON_SOURCE_ID
import com.example.commutingapp.utils.others.Constants.MAP_MARKER_IMAGE_ID
import com.example.commutingapp.utils.others.Constants.MINIMUM_MAP_LEVEL
import com.example.commutingapp.utils.others.Constants.SYMBOL_LAYER_ID
import com.example.commutingapp.utils.others.Constants.TRACKING_MAP_ZOOM
import com.google.gson.JsonObject
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.plugins.traffic.TrafficPlugin
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.PropertyValue
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

abstract class MapBox(private val view: View,private val activity: Activity):IMap<MapboxMap> {

    private var mapBoxMap:MapboxMap? = null
    private var mapBoxView: MapView = view.findViewById(R.id.googleMapView)
    private var mapBoxStyle: Style? = null
    private var searchLocationButton: AppCompatButton  = view.findViewById(R.id.buttonLocationSearch)
    private lateinit var home: CarmenFeature
    private lateinit var work: CarmenFeature


    override fun getMapInstance():MapboxMap? = mapBoxMap
    override fun deleteAllMapMarker(){
        initializeType(BuildConfig.MAP_STYLE)
    }

    override fun setupMap(savedInstanceState: Bundle?) {
         mapBoxView.apply {
             onCreate(savedInstanceState)
             isClickable = true
         }
    }

    override fun setupUI(mapType: String){
        mapBoxView.getMapAsync {map->
            mapBoxMap = map
                initializeMap()
                initializeType(mapType)
                initializeLocationPuck()
                initializeSearchFABLocation()
                initializeUserLocations()
                initializeMapMarkerImage()
        }
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
            mapBoxView.let { mapView ->
                TrafficPlugin(mapView, mapBoxMap!!, style).apply { setVisibility(true) }
                mapBoxStyle = style
            }
            setUpSource(style)
            setupLayer(style)

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

    override fun getLocationSearchResult(requestCode: Int,resultCode: Int, data: Intent?){

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_AUTOCOMPLETE) {
            val selectedCarmenFeature = PlaceAutocomplete.getPlace(data)
            mapBoxMap?.let { map->
                val source: GeoJsonSource? = map.style?.getSourceAs(GEO_JSON_SOURCE_LAYER_ID)
                source?.setGeoJson(FeatureCollection.fromFeatures(arrayOf(Feature.fromJson(selectedCarmenFeature.toJson()))))
                val location = LatLng((selectedCarmenFeature.geometry() as Point).latitude(),(selectedCarmenFeature.geometry() as Point).longitude())
                moveCameraToUser(location, TRACKING_MAP_ZOOM, DEFAULT_CAMERA_ANIMATION_DURATION)
            }
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
        LocationComponentOptions.builder(activity)
            .accuracyAlpha(0.3f)
            .compassAnimationEnabled(true)
            .accuracyAnimationEnabled(true)
            .build().also { componentOptions ->
                mapBoxMap?.locationComponent?.apply {
                    mapBoxStyle?.let {
                        activateLocationComponent(
                            LocationComponentActivationOptions.builder(activity, it)
                                .locationComponentOptions(componentOptions)
                                .build()
                        )
                        createComponentsLocation(this)
                    }
                }
            }
    }
    @SuppressLint("MissingPermission")
    private fun createComponentsLocation(locationComponent: LocationComponent) {
        locationComponent.apply {
            isLocationComponentEnabled = true
            renderMode = RenderMode.COMPASS
        }
    }

    private fun initializeSearchFABLocation(){
        searchLocationButton.setOnClickListener {
            val autoCompleteIntent = PlaceAutocomplete.IntentBuilder()
                .accessToken(activity.getString(R.string.MapsToken))
                .placeOptions(buildPlaceOptions())
                .build(activity)
                onSearchCompleted(autoCompleteIntent)
        }

    }
    abstract fun onSearchCompleted(intent:Intent)


    private fun buildPlaceOptions() =  PlaceOptions.builder()
        .backgroundColor(Color.parseColor("#EEEEEE"))
        .limit(10)
        .addInjectedFeature(home)
        .addInjectedFeature(work)
        .build(PlaceOptions.MODE_CARDS)

    private fun initializeUserLocations() {
        home = CarmenFeature.builder()
            .text("Mapbox SF Office")
            .geometry(Point.fromLngLat(-122.3964485, 37.7912561))
            .placeName("50 Beale St, San Francisco, CA")
            .id("mapbox-sf")
            .properties(JsonObject())
            .build()
        work = CarmenFeature.builder().text("Mapbox DC Office")
            .placeName("740 15th Street NW, Washington DC")
            .geometry(Point.fromLngLat(-77.0338348, 38.899750))
            .id("mapbox-dc")
            .properties(JsonObject())
            .build()
    }

    private fun setUpSource(loadedMapStyle: Style) {
        loadedMapStyle.addSource(GeoJsonSource(GEO_JSON_SOURCE_LAYER_ID))
        loadedMapStyle.addSource(GeoJsonSource(ICON_SOURCE_ID))
    }
    private fun setupLayer(loadedMapStyle: Style) {

        loadedMapStyle.apply {
            addLayer(SymbolLayer(SYMBOL_LAYER_ID,GEO_JSON_SOURCE_LAYER_ID).withProperties(*symbolLayerProperties()))
            addLayer(SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(*symbolLayerProperties()))
        }
    }
    private fun symbolLayerProperties()= arrayOf<PropertyValue<*>>(
        PropertyFactory.iconImage(MAP_MARKER_IMAGE_ID),
        PropertyFactory.iconOffset(arrayOf(0f, -8f)),
        PropertyFactory.iconSize(Constants.MAP_MARKER_SIZE)
    )

    override fun pointMapMarker(latLng: LatLng) {

        createMapMarker(latLng)
        mapBoxMap?.cameraPosition?.apply {
            moveCameraToUser(latLng, zoom, Constants.DEFAULT_CAMERA_ANIMATION_DURATION)
        }

    }

    private fun createMapMarker(latLng: LatLng) {

        val destinationLocation = Point.fromLngLat(latLng.longitude,latLng.latitude)
        val source:GeoJsonSource? = mapBoxMap?.style?.getSourceAs(ICON_SOURCE_ID)
        val feature:Feature = Feature.fromGeometry(destinationLocation)
        source?.let {
            it.setGeoJson(feature)
        }



    }
    override fun moveCameraToUser(latLng: LatLng,zoomLevel:Double,cameraAnimationDuration:Int) {
        mapBoxMap?.animateCamera(CameraUpdateFactory.newCameraPosition(buildCameraPosition(latLng,zoomLevel)), cameraAnimationDuration)
    }
    private fun buildCameraPosition(latLng: LatLng, zoomLevel: Double): CameraPosition =
        CameraPosition.Builder()
            .target(latLng)
            .zoom(zoomLevel)
            .tilt(Constants.CAMERA_TILT_DEGREES)
            .build()

}