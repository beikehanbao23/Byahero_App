package com.example.commutingapp.views.ui.mapComponents.maps

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.collection.size
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.BitmapConvert
import com.example.commutingapp.utils.others.Constants
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
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.plugins.traffic.TrafficPlugin
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource

abstract class MapBox(private val view: View,private val activity: Activity):IMap<MapboxMap> {

    private var mapBoxMap:MapboxMap? = null
    private var mapBoxView: MapView = view.findViewById(R.id.googleMapView)
    private var mapBoxStyle: Style? = null
    private lateinit var mapMarkerSymbol: SymbolManager
    private var searchLocationButton: AppCompatButton  = view.findViewById(R.id.buttonLocationSearch)
    private lateinit var home: CarmenFeature
    private lateinit var work: CarmenFeature


    override fun getMapInstance():MapboxMap? = mapBoxMap
    override fun deleteAllMapMarker() = mapMarkerSymbol.deleteAll()
    override fun setupMap(savedInstanceState: Bundle?,mapType: String) {
         mapBoxView.apply {
             onCreate(savedInstanceState)
             isClickable = true
         }.also { mapBoxView->
             mapBoxView.getMapAsync {map->
                 mapBoxMap = map
                 with(mapBoxMap!!){
                    uiSettings.isAttributionEnabled = false
                    uiSettings.isLogoEnabled = false
                    uiSettings.setCompassMargins(0,480,50,0)
                    setMaxZoomPreference(Constants.MINIMUM_MAP_LEVEL)
                    onMapReady(this)
                    addMapStyle(mapType)
                 }

             }
         }
    }
    abstract fun onMapReady(mapboxMap: MapboxMap)


    private fun addMapStyle(mapType:String){
        mapBoxMap?.setStyle(mapType) { style ->
            mapBoxView.let { mapView ->
                TrafficPlugin(mapView, mapBoxMap!!, style).apply { setVisibility(true) }
                mapBoxStyle = style
                createLocationPuck()
                mapMarkerSymbol = SymbolManager(mapView, mapBoxMap!!, mapBoxStyle!!)
            }

            initializeSearchFABLocation()
            addUserLocations()
            setupMapMarkerImage()
            setUpSource(style)
            setupLayer(style)
        }
    }

    override fun getLastKnownLocation():LatLng? = mapBoxMap?.locationComponent?.lastKnownLocation?.run {
        LatLng(this.latitude, this.longitude)
    }
    override fun recoverMissingMapMarker(){
        mapBoxView.addOnStyleImageMissingListener {
            setupMapMarkerImage()
        }
    }

    override fun updateMapStyle(style:String){
        mapBoxMap?.setStyle(style){ mapStyle -> mapBoxStyle = mapStyle}
    }
    override fun getLocationSearchResult(requestCode: Int,resultCode: Int, data: Intent?){

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CODE_AUTOCOMPLETE) {
            val selectedCarmenFeature = PlaceAutocomplete.getPlace(data)
            mapBoxMap?.let { map->
                val source: GeoJsonSource? = map.style?.getSourceAs(Constants.GEO_JSON_SOURCE_LAYER_ID)
                source?.setGeoJson(FeatureCollection.fromFeatures(arrayOf(Feature.fromJson(selectedCarmenFeature.toJson()))))
                val location = LatLng((selectedCarmenFeature.geometry() as Point).latitude(),(selectedCarmenFeature.geometry() as Point).longitude())
                moveCameraToUser(location,
                    Constants.TRACKING_MAP_ZOOM,
                    Constants.DEFAULT_CAMERA_ANIMATION_DURATION
                )
            }
        }

    }
    private  fun setupMapMarkerImage(){
        mapBoxStyle?.addImage(
            Constants.MAP_MARKER_IMAGE_NAME,
            BitmapConvert.getBitmapFromVectorDrawable(
                activity,
                R.drawable.ic_location_map_marker
            )
        )
    }
    override fun createLocationPuck() {
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

    private fun addUserLocations() {
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
        loadedMapStyle.addSource(GeoJsonSource(Constants.GEO_JSON_SOURCE_LAYER_ID))
    }
    private fun setupLayer(loadedMapStyle: Style) {
        loadedMapStyle.addLayer(
            SymbolLayer(Constants.SYMBOL_LAYER_ID, Constants.GEO_JSON_SOURCE_LAYER_ID).withProperties(
                PropertyFactory.iconImage(Constants.MAP_MARKER_IMAGE_NAME),
                PropertyFactory.iconOffset(arrayOf(0f, -8f)),
                PropertyFactory.iconSize(Constants.MAP_MARKER_SIZE)
            )
        )
    }

    override fun pointMapMarker(latLng: LatLng) {
        if (hasExistingMapMarker()) {
            mapMarkerSymbol.deleteAll()
        }
        createMapMarker(latLng)
        mapBoxMap?.cameraPosition?.apply {
            moveCameraToUser(latLng, zoom, Constants.DEFAULT_CAMERA_ANIMATION_DURATION)
        }

    }

    private fun hasExistingMapMarker() = mapMarkerSymbol.annotations.size != 0

    private fun createMapMarker(latLng: LatLng) {
        mapMarkerSymbol.create(
            SymbolOptions()
                .withLatLng(latLng)
                .withIconImage(Constants.MAP_MARKER_IMAGE_NAME)
                .withIconSize(Constants.MAP_MARKER_SIZE)
        )

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