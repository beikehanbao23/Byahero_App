package com.example.commutingapp.views.ui.fragments


import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.*
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.collection.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.commutingapp.BuildConfig.MAP_STYLE
import com.example.commutingapp.R
import com.example.commutingapp.data.service.TrackingService
import com.example.commutingapp.data.service.innerPolyline
import com.example.commutingapp.databinding.CommuterFragmentBinding
import com.example.commutingapp.utils.InternetConnection.Connection
import com.example.commutingapp.utils.others.BitmapConvert.getBitmapFromVectorDrawable
import com.example.commutingapp.utils.others.Constants
import com.example.commutingapp.utils.others.Constants.ACTION_PAUSE_SERVICE
import com.example.commutingapp.utils.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.commutingapp.utils.others.Constants.CAMERA_TILT_DEGREES
import com.example.commutingapp.utils.others.Constants.DEFAULT_CAMERA_ANIMATION_DURATION
import com.example.commutingapp.utils.others.Constants.DEFAULT_LATITUDE
import com.example.commutingapp.utils.others.Constants.DEFAULT_LONGITUDE
import com.example.commutingapp.utils.others.Constants.DEFAULT_MAP_ZOOM
import com.example.commutingapp.utils.others.Constants.ULTRA_FAST_CAMERA_ANIMATION_DURATION
import com.example.commutingapp.utils.others.Constants.FILE_NAME_MAPS_TYPE_SHARED_PREFERENCE
import com.example.commutingapp.utils.others.Constants.INVISIBLE_BOTTOM_SHEET_PEEK_HEIGHT
import com.example.commutingapp.utils.others.Constants.LAST_KNOWN_LOCATION_MAP_ZOOM
import com.example.commutingapp.utils.others.Constants.MAP_MARKER_IMAGE_NAME
import com.example.commutingapp.utils.others.Constants.MAP_MARKER_SIZE
import com.example.commutingapp.utils.others.Constants.MINIMUM_MAP_LEVEL
import com.example.commutingapp.utils.others.Constants.POLYLINE_COLOR
import com.example.commutingapp.utils.others.Constants.POLYLINE_WIDTH
import com.example.commutingapp.utils.others.Constants.REQUEST_CHECK_SETTING
import com.example.commutingapp.utils.others.Constants.TEN_METERS
import com.example.commutingapp.utils.others.Constants.TRACKING_MAP_ZOOM
import com.example.commutingapp.utils.others.Constants.LOW_VISIBLE_BOTTOM_SHEET_PEEK_HEIGHT
import com.example.commutingapp.utils.others.FragmentToActivity
import com.example.commutingapp.utils.others.TrackingPermissionUtility.hasLocationPermission
import com.example.commutingapp.utils.others.TrackingPermissionUtility.requestPermission
import com.example.commutingapp.viewmodels.MainViewModel
import com.example.commutingapp.views.dialogs.CustomDialogBuilder
import com.example.commutingapp.views.dialogs.DialogDirector
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException

import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.mapboxsdk.annotations.PolylineOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.plugins.traffic.TrafficPlugin
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import android.content.Intent

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color

import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.widget.ImageViewCompat
import com.example.commutingapp.utils.others.Constants.CAMERA_ZOOM_MAP_MARKER
import com.example.commutingapp.utils.others.Constants.FAST_CAMERA_ANIMATION_DURATION
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.google.gson.JsonObject

import com.mapbox.api.geocoding.v5.models.CarmenFeature

import com.mapbox.geojson.Point
import com.example.commutingapp.utils.others.Constants.GEO_JSON_SOURCE_LAYER_ID
import com.example.commutingapp.utils.others.Constants.REQUEST_CODE_AUTOCOMPLETE
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource


import androidx.appcompat.widget.AppCompatButton
import com.google.android.gms.location.*
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*


@AndroidEntryPoint
class CommuterFragment : Fragment(R.layout.commuter_fragment), EasyPermissions.PermissionCallbacks,
    OnMapReadyCallback, MapboxMap.OnMapLongClickListener, MapboxMap.OnMapClickListener,MapboxMap.OnMoveListener {

    private val mainViewModel: MainViewModel by viewModels()
    private var mapBoxMap: MapboxMap? = null
    private var isTracking = false
    private var outerPolyline = mutableListOf<innerPolyline>()
    private var mapBoxView: MapView? = null

    private lateinit var startButton: Button
    private lateinit var directionButton: Button
    private lateinit var saveButton: Button
    private lateinit var shareButton: Button
    private lateinit var dialogDirector: DialogDirector
    private lateinit var commuterFragmentBinding: CommuterFragmentBinding
    private lateinit var searchLocationButton: AppCompatButton
    private var mapBoxStyle: Style? = null
    private lateinit var mapMarkerSymbol: SymbolManager
    private lateinit var startingBottomSheet: BottomSheetBehavior<View>
    private lateinit var trackingBottomSheet: BottomSheetBehavior<View>
    private lateinit var notifyListener: FragmentToActivity
    private lateinit var home: CarmenFeature
    private lateinit var work: CarmenFeature
    private lateinit var preferences:SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        commuterFragmentBinding = CommuterFragmentBinding.inflate(inflater,container,false)
        return commuterFragmentBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeComponents(view)
        hideStartingBottomSheet()
        showBottomNavigation()
        hideTrackingBottomSheet()
        provideClickListeners()
        setupMapBoxView(savedInstanceState)
        subscribeToObservers()
        recoverMissingMapMarker()
        updateLocationFloatingButtonIcon()
    }
    private fun recoverMissingMapMarker(){
        mapBoxView?.addOnStyleImageMissingListener {
            addMapMarkerImage(MAP_MARKER_IMAGE_NAME)
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            this.notifyListener = context as FragmentToActivity
        } catch (e: ClassCastException) { }
    }


    private fun showBottomSheet() {
        provideBottomSheetsButton()
        startingBottomSheet.peekHeight = LOW_VISIBLE_BOTTOM_SHEET_PEEK_HEIGHT
    }

    private fun hideStartingBottomSheet() {
        startingBottomSheet.peekHeight = INVISIBLE_BOTTOM_SHEET_PEEK_HEIGHT
    }
    private fun showTrackingBottomSheet(){
        trackingBottomSheet.peekHeight = 300//todo
    }
    private fun hideTrackingBottomSheet(){
        trackingBottomSheet.peekHeight = INVISIBLE_BOTTOM_SHEET_PEEK_HEIGHT
    }
    private fun showLocationFloatingButton(){
        commuterFragmentBinding.floatingActionButtonLocation.visibility = View.VISIBLE
    }
    private fun hideLocationFloatingButton(){
        commuterFragmentBinding.floatingActionButtonLocation.visibility = View.GONE
    }
    private fun showBottomNavigation(){
        notifyListener.onSecondNotify()
    }
    private fun hideBottomNavigation(){
        notifyListener.onFirstNotify()
    }

    private fun provideBottomSheetsButton() {

    if(!Connection.hasInternetConnection(requireContext()) && Connection.hasGPSConnection(requireContext())){
        showNoInternetAndHasGpsBottomSheetLayout()
        return
    }
    if(Connection.hasInternetConnection(requireContext()) && !Connection.hasGPSConnection(requireContext())){
        showHasInternetAndNoGpsBottomSheetLayout()
        return
    }
    if(!Connection.hasInternetConnection(requireContext()) && !Connection.hasGPSConnection(requireContext())){
        showNoInternetAndNoGpsBottomSheetLayout()
        return
    }

        showDefaultBottomSheetLayout()
    }

    private fun showNoInternetAndHasGpsBottomSheetLayout() {
        shareButton.visibility = View.GONE
        saveButton.visibility = View.GONE
        directionButton.visibility = View.VISIBLE
        startButton.visibility = View.VISIBLE
    }

    private fun showHasInternetAndNoGpsBottomSheetLayout() {
        startButton.visibility = View.GONE
        saveButton.visibility = View.VISIBLE
        shareButton.visibility = View.VISIBLE
        directionButton.visibility = View.VISIBLE
    }

    private fun showNoInternetAndNoGpsBottomSheetLayout() {
        startButton.visibility = View.GONE
        saveButton.visibility = View.GONE
        shareButton.visibility = View.GONE
        directionButton.visibility = View.VISIBLE
    }

    private fun showDefaultBottomSheetLayout() {
        saveButton.visibility = View.VISIBLE
        shareButton.visibility = View.VISIBLE
        directionButton.visibility = View.VISIBLE
        startButton.visibility = View.VISIBLE

    }

    private fun initializeComponents(view: View) {
        preferences = requireContext().getSharedPreferences(FILE_NAME_MAPS_TYPE_SHARED_PREFERENCE,Context.MODE_PRIVATE)
        dialogDirector = DialogDirector(requireActivity())
        mapBoxView = view.findViewById(R.id.googleMapView)
        startButton = view.findViewById(R.id.startButton)
        directionButton = view.findViewById(R.id.directionsButton)
        saveButton = view.findViewById(R.id.saveButton)
        shareButton = view.findViewById(R.id.shareButton)
        searchLocationButton = view.findViewById(R.id.buttonLocationSearch)
        startingBottomSheet = BottomSheetBehavior.from(view.findViewById(R.id.bottomSheetNormalState)).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
        trackingBottomSheet = BottomSheetBehavior.from(view.findViewById(R.id.bottomSheetTrackingState)).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            isHideable=false

        }
    }

    private fun setupMapBoxView(savedInstanceState: Bundle?) {
        mapBoxView?.apply {
            onCreate(savedInstanceState)
            isClickable = true
        }?.also {
            it.getMapAsync(this)
        }
    }

    private fun provideClickListeners() {

        commuterFragmentBinding.floatingActionButtonChooseMap.setOnClickListener {
            dialogDirector.constructChooseMapDialog().apply {
                createMapTypeIndicator(this)
                setMapTypeListeners(this)
                show()
            } }

        commuterFragmentBinding.floatingActionButtonLocation.setOnClickListener {

            mapBoxMap?.locationComponent?.lastKnownLocation?.let { location ->
                moveCameraToUser(LatLng(location.latitude, location.longitude), CAMERA_ZOOM_MAP_MARKER, FAST_CAMERA_ANIMATION_DURATION)
                changeFloatingButtonIconBlue()
            }

            if(Connection.hasInternetConnection(requireContext()) && !Connection.hasGPSConnection(requireContext())){
                checkLocationSetting()
            }
        }

        startButton.setOnClickListener {
            if (requestPermissionGranted()) {
                showTrackingBottomSheet()
                hideStartingBottomSheet()
                hideBottomNavigation()
                hideLocationFloatingButton()
                checkLocationSetting()

            }
        }

        directionButton.setOnClickListener {

        }
        saveButton.setOnClickListener {

        }
        shareButton.setOnClickListener {

        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION).apply {
            addAction(Intent.ACTION_PROVIDER_CHANGED)
            requireActivity().registerReceiver(locationSwitchStateReceiver(), this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(locationSwitchStateReceiver())
    }

    private fun locationSwitchStateReceiver()=
        object: BroadcastReceiver(){
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceive(context: Context?, intent: Intent?) {
                if(LocationManager.PROVIDERS_CHANGED_ACTION == intent?.action){
                    updateLocationFloatingButtonIcon()
                }
            }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun updateLocationFloatingButtonIcon()=
        if(Connection.hasGPSConnection(requireContext())) changeFloatingButtonIconBlack() else changeFloatingButtonIconRed()

    private fun changeFloatingButtonIconRed(){
        changeLocationFloatingButtonIconColor(Color.RED)
        changeLocationFloatingButtonIcon(R.drawable.ic_location_asking)
    }
    private fun changeFloatingButtonIconBlack(){
        changeLocationFloatingButtonIconColor(Color.BLACK)
        changeLocationFloatingButtonIcon(R.drawable.ic_baseline_my_location_24)
    }
    private fun changeFloatingButtonIconBlue(){
        changeLocationFloatingButtonIconColor(Color.BLUE)
        changeLocationFloatingButtonIcon(R.drawable.ic_baseline_my_location_24)
    }
    private fun changeLocationFloatingButtonIconColor(@ColorInt color:Int){
        ImageViewCompat.setImageTintList(
            commuterFragmentBinding.floatingActionButtonLocation,
            ColorStateList.valueOf(color))

    }
    private fun changeLocationFloatingButtonIcon(@DrawableRes imageId:Int){
        commuterFragmentBinding.floatingActionButtonLocation.setImageResource(imageId)
    }
    private fun setMapTypeListeners(customDialogBuilder: CustomDialogBuilder) {
        customDialogBuilder.apply {

            findViewById<View>(R.id.defaultMapStyleButton)?.setOnClickListener {
                changeMapType(this,Style.TRAFFIC_DAY)
            }
            findViewById<View>(R.id.trafficNightMapStyleButton)?.setOnClickListener {
                changeMapType(this,Style.TRAFFIC_NIGHT)
            }
            findViewById<View>(R.id.darkMapStyleButton)?.setOnClickListener {
                changeMapType(this,Style.DARK)
            }
            findViewById<View>(R.id.streetMapStyleButton)?.setOnClickListener {
                changeMapType(this,Style.MAPBOX_STREETS)
            }
            findViewById<View>(R.id.satelliteStreetsMapStyleButton)?.setOnClickListener {
                changeMapType(this,Style.SATELLITE_STREETS)
            }
            findViewById<View>(R.id.outdoorsMapStyleButton)?.setOnClickListener {
                changeMapType(this,Style.OUTDOORS)
            }
            findViewById<View>(R.id.lightMapStyle)?.setOnClickListener {
                changeMapType(this,Style.LIGHT)
            }
            findViewById<View>(R.id.neonMapStyleButton)?.setOnClickListener {
                changeMapType(this,MAP_STYLE)
            }
            findViewById<View>(R.id.satelliteMapStyleButton)?.setOnClickListener {
                changeMapType(this,Style.SATELLITE)
            }
        }.also {
            recoverMissingMapMarker()
        }
    }
    private fun createMapTypeIndicator(customDialogBuilder: CustomDialogBuilder){
        customDialogBuilder.apply {
        findViewById<View>(getMapTypeButtons().getValue(getMapType()))?.setBackgroundResource(R.drawable.map_type_visible_image_button_background)
        }
    }

    private fun removePreviousMapTypeIndicator(customDialogBuilder: CustomDialogBuilder){
        customDialogBuilder.apply {
       for(i in getMapTypeButtons()){
           if(i.key == getMapType()){
               continue
           }else{
               findViewById<View>(i.value)?.setBackgroundColor(Color.WHITE)
           }
       }
        }
    }

    private fun getMapTypeButtons():HashMap<String,Int> =
    HashMap<String,Int>().apply {
        this[Style.TRAFFIC_DAY] = R.id.defaultMapStyleButton
        this[Style.TRAFFIC_NIGHT] = R.id.trafficNightMapStyleButton
        this[Style.DARK] = R.id.darkMapStyleButton
        this[Style.MAPBOX_STREETS] = R.id.streetMapStyleButton
        this[Style.SATELLITE_STREETS] = R.id.satelliteStreetsMapStyleButton
        this[Style.OUTDOORS] = R.id.outdoorsMapStyleButton
        this[Style.LIGHT] = R.id.lightMapStyle
        this[MAP_STYLE] = R.id.neonMapStyleButton
        this[Style.SATELLITE] = R.id.satelliteMapStyleButton

    }
    @SuppressLint("MissingPermission")
    private fun moveCameraToLastKnownLocation() {
        LocationServices.getFusedLocationProviderClient(requireActivity()).apply {
            lastLocation.addOnSuccessListener {
                it?.let { latLng ->
                    moveCameraToUser(
                        LatLng(latLng.latitude, latLng.longitude),
                        LAST_KNOWN_LOCATION_MAP_ZOOM,
                        ULTRA_FAST_CAMERA_ANIMATION_DURATION
                    )
                }
            }
            lastLocation.addOnFailureListener {
                moveCameraToUser(
                    LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE), DEFAULT_MAP_ZOOM,
                    DEFAULT_CAMERA_ANIMATION_DURATION
                )
            }
        }
    }

    companion object {
        val request: LocationRequest = LocationRequest.create().apply {
            interval = Constants.NORMAL_LOCATION_UPDATE_INTERVAL
            smallestDisplacement = TEN_METERS
            fastestInterval = Constants.FASTEST_LOCATION_UPDATE_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

    }

    var locationRequest: LocationRequest = request

    private fun checkLocationSetting() {

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(request)
            .setAlwaysShow(true)

        LocationServices.getSettingsClient(requireContext())
            .checkLocationSettings(builder.build()).apply {
                getLocationSettingResult(this)
            }


    }

    private fun getLocationSettingResult(result: Task<LocationSettingsResponse>) {
        result.addOnCompleteListener {
            try {
                it.getResult(ApiException::class.java)

            } catch (e: ApiException) {
                handleLocationResultException(e)
            }
        }
    }

    private fun handleLocationResultException(e: ApiException) {
        when (e.statusCode) {
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                (e as ResolvableApiException).apply {
                startIntentSenderForResult(this.resolution.intentSender, REQUEST_CHECK_SETTING, null, 0, 0, 0, null)
                } }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        getGPSDialogSettingResult(requestCode, resultCode)
        getLocationSearchResult(requestCode, resultCode, data)


    }

    private fun getLocationSearchResult(requestCode: Int,resultCode: Int, data:Intent?){

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            val selectedCarmenFeature = PlaceAutocomplete.getPlace(data)
            mapBoxMap?.let { map->
                val source: GeoJsonSource = map.style?.getSourceAs(GEO_JSON_SOURCE_LAYER_ID)!!
                source.setGeoJson(FeatureCollection.fromFeatures(arrayOf(Feature.fromJson(selectedCarmenFeature.toJson()))))
                val location = LatLng((selectedCarmenFeature.geometry() as Point).latitude(),(selectedCarmenFeature.geometry() as Point).longitude())
                moveCameraToUser(location,TRACKING_MAP_ZOOM, DEFAULT_CAMERA_ANIMATION_DURATION)
            }
        }
    }
    private fun getGPSDialogSettingResult(requestCode: Int,resultCode: Int){
        when (requestCode) {
            REQUEST_CHECK_SETTING ->
                when (resultCode) {
                    RESULT_OK -> {
                        //toggleStartButton()
                    }
                    RESULT_CANCELED->{

                    }
                }
        }
    }
    override fun onMapReady(mapboxMap: MapboxMap) {
        mapBoxMap = mapboxMap.apply {
            uiSettings.isAttributionEnabled = false
            uiSettings.isLogoEnabled = false
        }

        mapBoxMap?.addOnMapLongClickListener(this)
        mapBoxMap?.addOnMapClickListener(this)
        mapBoxMap?.addOnMoveListener(this)
        setMapZoomLevel()
        addMapStyle(getMapType())
        moveCameraToLastKnownLocation()
        addAllPolyLines()
    }
    private fun initializeSearchFABLocation(){
        searchLocationButton.setOnClickListener {
            val autoCompleteIntent = PlaceAutocomplete.IntentBuilder()
                .accessToken(requireContext().getString(R.string.MapsToken))
                .placeOptions(buildPlaceOptions())
                .build(requireActivity())
            startActivityForResult(autoCompleteIntent, REQUEST_CODE_AUTOCOMPLETE);
        }
    }
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
    private fun setMapZoomLevel() {
        mapBoxMap?.setMaxZoomPreference(MINIMUM_MAP_LEVEL)
    }

    private fun addAllPolyLines() {
        outerPolyline.forEach {
            customPolylineAppearance().addAll(it).apply {
                mapBoxMap?.addPolyline(this)
            }
        }
    }

    private fun addMapStyle(mapType: String) {
        mapBoxMap?.setStyle(mapType) { style ->
            initializeSearchFABLocation()
            addUserLocations()
            style.addImage(MAP_MARKER_IMAGE_NAME,getBitmapFromVectorDrawable(requireContext(), R.drawable.ic_location_map_marker))
            setUpSource(style)
            setupLayer(style)
            mapBoxView?.let { mapView ->
                TrafficPlugin(mapView, mapBoxMap!!, style).apply { setVisibility(true) }
                mapBoxStyle = style
                createLocationPuck(style)
                mapMarkerSymbol = SymbolManager(mapView, mapBoxMap!!, mapBoxStyle!!)
            }
        }
    }
    private fun setUpSource(loadedMapStyle: Style) {
        loadedMapStyle.addSource(GeoJsonSource(GEO_JSON_SOURCE_LAYER_ID))
    }
    private fun setupLayer(loadedMapStyle: Style) {
        loadedMapStyle.addLayer(
            SymbolLayer("SYMBOL_LAYER_ID", GEO_JSON_SOURCE_LAYER_ID).withProperties(
                iconImage(MAP_MARKER_IMAGE_NAME),
                iconOffset(arrayOf(0f, -8f)),
                iconSize(MAP_MARKER_SIZE)
            )
        )
    }
    private fun changeMapType(customDialogBuilder: CustomDialogBuilder,mapType: String) {
        mapBoxMap?.setStyle(mapType){ mapBoxStyle = it }

        saveMapTypeToSharedPreference(mapType)
        removePreviousMapTypeIndicator(customDialogBuilder)
        createMapTypeIndicator(customDialogBuilder)
    }
    private fun saveMapTypeToSharedPreference(mapType:String){
        preferences.edit().apply{
            putString(FILE_NAME_MAPS_TYPE_SHARED_PREFERENCE,mapType)
            apply()
        }

    }
    private fun getMapType():String {return preferences.getString(FILE_NAME_MAPS_TYPE_SHARED_PREFERENCE,Style.TRAFFIC_DAY).toString()}

    override fun onMapLongClick(point: LatLng): Boolean {

        pointMapMarker(point)
        showBottomSheet()
        hideBottomNavigation()
        return true
    }

    override fun onMapClick(point: LatLng): Boolean {
        mapMarkerSymbol.deleteAll()
        hideStartingBottomSheet()
        showBottomNavigation()
        return true
    }

    private fun addMapMarkerImage(imageName:String){
        mapBoxStyle?.addImage(imageName, getBitmapFromVectorDrawable(requireContext(), R.drawable.ic_location_map_marker)
        )
    }
    private fun pointMapMarker(latLng: LatLng) {
        addMapMarkerImage(MAP_MARKER_IMAGE_NAME)

        if (hasExistingMapMarker()) {
            mapMarkerSymbol.deleteAll()
        }
        createMapMarker(latLng)
        mapBoxMap?.cameraPosition?.apply {
            moveCameraToUser(latLng, zoom, DEFAULT_CAMERA_ANIMATION_DURATION)
        }

    }

    private fun hasExistingMapMarker() = mapMarkerSymbol.annotations.size != 0

    private fun createMapMarker(latLng: LatLng) {
        mapMarkerSymbol.create(
            SymbolOptions()
                .withLatLng(latLng)
                .withIconImage(MAP_MARKER_IMAGE_NAME)
                .withIconSize(MAP_MARKER_SIZE)
        )

    }


    private fun createLocationPuck(style: Style) {
        LocationComponentOptions.builder(requireContext())
            .accuracyAlpha(0.3f)
            .compassAnimationEnabled(true)
            .accuracyAnimationEnabled(true)
            .build().also { componentOptions ->
                mapBoxMap?.locationComponent?.apply {
                    activateLocationComponent(LocationComponentActivationOptions.builder(requireContext(), style)
                            .locationComponentOptions(componentOptions)
                            .build())
                    createComponentsLocation(this)
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

    private fun subscribeToObservers() {
        TrackingService().isCurrentlyTracking().observe(viewLifecycleOwner) {
            isTracking = it
            mapBoxStyle?.let { style -> createLocationPuck(style) }
            updateButtons()

        }

        TrackingService().outerPolyline().observe(viewLifecycleOwner) {
            outerPolyline = it

            addLatestPolyline()
            if (hasExistingInnerAndOuterPolyLines()) {
                moveCameraToUser(
                    outerPolyline.last().last(), TRACKING_MAP_ZOOM,
                    DEFAULT_CAMERA_ANIMATION_DURATION
                )
            }
        }

        /*
        TrackingService.timeInMillis.observe(viewLifecycleOwner) {
            //TODO implement later
        }
         */

    }

    private fun sendCommandToTrackingService(action: String) {
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    private fun toggleStartButton() {
        if (isTracking) {
            sendCommandToTrackingService(ACTION_PAUSE_SERVICE)
            return
        }
        sendCommandToTrackingService(ACTION_START_OR_RESUME_SERVICE)
    }

    private fun updateButtons() {

        if (isTracking) {
            startButton.text = getString(R.string.stopButton)
            return
        }
        startButton.text = getString(R.string.startButton)
    }


    private fun moveCameraToUser(latLng: LatLng,zoomLevel:Double,cameraAnimationDuration:Int) {
        mapBoxMap?.animateCamera(CameraUpdateFactory.newCameraPosition(buildCameraPosition(latLng,zoomLevel)), cameraAnimationDuration)
    }

    private fun buildCameraPosition(latLng: LatLng, zoomLevel: Double): CameraPosition =
        CameraPosition.Builder()
            .target(latLng)
            .zoom(zoomLevel)
            .tilt(CAMERA_TILT_DEGREES)
            .build()
    private fun addLatestPolyline() {
        if (hasExistingInnerPolyLines()) {
            val innerPolylinePosition = outerPolyline.last().size - 2
            val preLastLatLng = outerPolyline.last()[innerPolylinePosition]
            val lastLatLng = outerPolyline.last().last()

            customPolylineAppearance()
                .add(preLastLatLng)
                .add(lastLatLng).apply {
                    mapBoxMap?.addPolyline(this)
                }
        }
    }


    private fun customPolylineAppearance(): PolylineOptions {

        return PolylineOptions()
            .color(POLYLINE_COLOR)
            .width(POLYLINE_WIDTH)


    }
    private fun hasExistingInnerAndOuterPolyLines() =
        outerPolyline.isNotEmpty() && outerPolyline.last().isNotEmpty()

    private fun hasExistingInnerPolyLines() =
        outerPolyline.isNotEmpty() && outerPolyline.last().size > 1


    private fun requestPermissionGranted(): Boolean {
        if (hasLocationPermission(requireContext())) {
            return true
        }
        requestPermission(this)
        return false

    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermission(this)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        @Suppress("DEPRECATION")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onMoveBegin(detector: MoveGestureDetector){}

    @SuppressLint("ResourceAsColor")
    override fun onMove(detector: MoveGestureDetector) {
        changeFloatingButtonIconBlack()
    }

    override fun onMoveEnd(detector: MoveGestureDetector) {}


}