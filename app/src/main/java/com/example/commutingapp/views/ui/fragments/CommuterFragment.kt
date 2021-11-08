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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.commutingapp.R
import com.example.commutingapp.data.service.TrackingService
import com.example.commutingapp.data.service.innerPolyline
import com.example.commutingapp.databinding.CommuterFragmentBinding
import com.example.commutingapp.utils.InternetConnection.Connection
import com.example.commutingapp.utils.others.Constants
import com.example.commutingapp.utils.others.Constants.ACTION_PAUSE_SERVICE
import com.example.commutingapp.utils.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.commutingapp.utils.others.Constants.DEFAULT_CAMERA_ANIMATION_DURATION
import com.example.commutingapp.utils.others.Constants.DEFAULT_LATITUDE
import com.example.commutingapp.utils.others.Constants.DEFAULT_LONGITUDE
import com.example.commutingapp.utils.others.Constants.DEFAULT_MAP_ZOOM
import com.example.commutingapp.utils.others.Constants.ULTRA_FAST_CAMERA_ANIMATION_DURATION
import com.example.commutingapp.utils.others.Constants.LAST_KNOWN_LOCATION_MAP_ZOOM
import com.example.commutingapp.utils.others.Constants.POLYLINE_COLOR
import com.example.commutingapp.utils.others.Constants.POLYLINE_WIDTH
import com.example.commutingapp.utils.others.Constants.REQUEST_CHECK_SETTING
import com.example.commutingapp.utils.others.Constants.TEN_METERS
import com.example.commutingapp.utils.others.Constants.TRACKING_MAP_ZOOM
import com.example.commutingapp.utils.others.FragmentToActivity
import com.example.commutingapp.utils.others.TrackingPermissionUtility.hasLocationPermission
import com.example.commutingapp.utils.others.TrackingPermissionUtility.requestPermission
import com.example.commutingapp.viewmodels.MainViewModel
import com.example.commutingapp.views.dialogs.CustomDialogBuilder
import com.example.commutingapp.views.dialogs.DialogDirector
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.tasks.Task
import com.mapbox.mapboxsdk.annotations.PolylineOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.commutingapp.utils.others.Constants.CAMERA_ZOOM_MAP_MARKER
import com.example.commutingapp.utils.others.Constants.FAST_CAMERA_ANIMATION_DURATION
import com.mapbox.android.gestures.MoveGestureDetector
import com.example.commutingapp.utils.others.Constants.REQUEST_CODE_AUTOCOMPLETE
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.example.commutingapp.views.ui.subComponents.maps.MapBox
import com.example.commutingapp.views.ui.subComponents.maps.MapWrapper
import com.example.commutingapp.views.ui.subComponents.BottomNavigation
import com.example.commutingapp.views.ui.subComponents.Component
import com.example.commutingapp.views.ui.subComponents.FAB.FloatingActionButtonLocation
import com.example.commutingapp.views.ui.subComponents.FAB.FloatingActionButtonMapType
import com.google.android.gms.location.*
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.example.commutingapp.views.ui.subComponents.StartingBottomSheet
import com.example.commutingapp.views.ui.subComponents.TrackingBottomSheet
import com.mapbox.mapboxsdk.maps.MapView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CommuterFragment : Fragment(R.layout.commuter_fragment), EasyPermissions.PermissionCallbacks,
     MapboxMap.OnMapLongClickListener, MapboxMap.OnMapClickListener,MapboxMap.OnMoveListener {

    private val mainViewModel: MainViewModel by viewModels()

    private var isTracking = false
    private var outerPolyline = mutableListOf<innerPolyline>()


    private lateinit var startButton: Button
    private lateinit var directionButton: Button
    private lateinit var saveButton: Button
    private lateinit var shareButton: Button
    private lateinit var dialogDirector: DialogDirector
    private lateinit var commuterFragmentBinding: CommuterFragmentBinding
    private lateinit var searchLocationButton: AppCompatButton
    private lateinit var notifyListener: FragmentToActivity
    private lateinit var normalBottomSheet: Component
    private lateinit var trackingBottomSheet:Component
    private lateinit var bottomNavigation:Component
    private lateinit var locationFAB:FloatingActionButtonLocation
    private lateinit var mapTypeFAB:FloatingActionButtonMapType
    private lateinit var map:MapWrapper<MapboxMap,MapView>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        commuterFragmentBinding = CommuterFragmentBinding.inflate(inflater,container,false)
        return commuterFragmentBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeComponents(view)
        normalBottomSheet.hide()
        bottomNavigation.show()
        trackingBottomSheet.hide()
        provideClickListeners()
        map.getMapView().apply {
            onCreate(savedInstanceState)
            isClickable = true
        }
        map.setupUI(mapTypeFAB.loadMapType())
        subscribeToObservers()
        map.recoverMissingMapMarker()
        locationFAB.updateLocationFloatingButtonIcon()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            this.notifyListener = context as FragmentToActivity
        } catch (e: ClassCastException) { }
    }


    private fun initializeComponents(view: View) {
        dialogDirector = DialogDirector(requireActivity())

        startButton = view.findViewById(R.id.startButton)
        directionButton = view.findViewById(R.id.directionsButton)
        saveButton = view.findViewById(R.id.saveButton)
        shareButton = view.findViewById(R.id.shareButton)
        searchLocationButton = view.findViewById(R.id.buttonLocationSearch)
        normalBottomSheet = Component(StartingBottomSheet(view,requireContext()))
        trackingBottomSheet = Component(TrackingBottomSheet(view))
        bottomNavigation = Component(BottomNavigation(notifyListener))
        locationFAB = FloatingActionButtonLocation(commuterFragmentBinding,requireContext())
        mapTypeFAB = FloatingActionButtonMapType(requireContext())
        val mapbox = object : MapBox(view,requireActivity()){

            override fun onMapReady(mapboxMap: MapboxMap) {
                mapboxMap.addOnMapLongClickListener(this@CommuterFragment)
                mapboxMap.addOnMapClickListener(this@CommuterFragment)
                mapboxMap.addOnMoveListener(this@CommuterFragment)
                moveCameraToLastKnownLocation()
                addAllPolyLines()
            }

            override fun onSearchCompleted(intent: Intent) {
            startActivityForResult(intent,REQUEST_CODE_AUTOCOMPLETE)
            }
        }
        map = MapWrapper(mapbox)
    }


    private fun provideClickListeners() {
        provideMapTypeDialogListener()
        provideLocationButtonListener()
        provideStartButtonListener()
        provideDirectionButtonListener()
        provideSaveButtonListener()
        provideShareButtonListener()

    }
    private fun provideMapTypeDialogListener(){
        commuterFragmentBinding.floatingActionButtonChooseMap.setOnClickListener {
            dialogDirector.constructChooseMapDialog().apply {
                mapTypeFAB.createMapTypeIndicator(this)
                setMapTypeListeners(this)
                show()
            } }
    }
    private fun provideLocationButtonListener(){
        commuterFragmentBinding.floatingActionButtonLocation.setOnClickListener {
            if(requestPermissionGranted()) {
                map.getLastKnownLocation()?.let { location -> map.moveCameraToUser(location, CAMERA_ZOOM_MAP_MARKER, FAST_CAMERA_ANIMATION_DURATION) }
                    locationFAB.changeFloatingButtonIconBlue()

                if (Connection.hasInternetConnection(requireContext()) && !Connection.hasGPSConnection(requireContext())) {
                    checkLocationSetting().apply {
                        this.addOnCompleteListener {
                            try{ it.getResult(ApiException::class.java) }catch (e:ApiException){ }
                        }
                    }
                }
            }
        }
    }
    private fun provideStartButtonListener(){
        startButton.setOnClickListener {
            if (requestPermissionGranted()) {
                trackingBottomSheet.show()
                normalBottomSheet.hide()
                bottomNavigation.hide()
                locationFAB.hideLocationFloatingButton()
                checkLocationSetting().addOnCompleteListener {
                       try{
                           it.getResult(ApiException::class.java)
                           toggleStartButton()
                       }catch (e:ApiException){
                         handleLocationResultException(e)
                       }
                }

            }
            }
        }
    private fun provideDirectionButtonListener(){
        directionButton.setOnClickListener {

        }
    }
    private fun provideSaveButtonListener(){
        saveButton.setOnClickListener {

        }
    }
    private fun provideShareButtonListener(){
        shareButton.setOnClickListener {

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver()
    }
    private fun registerReceiver(){
        try {
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION).apply {
                addAction(Intent.ACTION_PROVIDER_CHANGED)
                requireActivity().registerReceiver(locationSwitchStateReceiver(), this)
            }
        }catch (e:IllegalArgumentException){}
    }
    private fun unregisterReceiver(){
        try {
        requireActivity().unregisterReceiver(locationSwitchStateReceiver())
        }catch (e:IllegalArgumentException){}
    }



    private fun locationSwitchStateReceiver()= object: BroadcastReceiver(){
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceive(context: Context?, intent: Intent?) {
                if(LocationManager.PROVIDERS_CHANGED_ACTION == intent?.action){
                    locationFAB.updateLocationFloatingButtonIcon()
                }
            }

    }
    private fun setMapTypeListeners(customDialogBuilder: CustomDialogBuilder) {
        customDialogBuilder.also { mapTypeListener->
            mapTypeFAB.getMapTypeButtons().forEach { hashMap->
               mapTypeListener.findViewById<View>(hashMap.value )?.setOnClickListener {
                   mapTypeFAB.changeMapType(mapTypeListener,hashMap.key)
                   map.updateMapStyle(hashMap.key)
               }
           }
        }.also {
            map.recoverMissingMapMarker()
        }
    }



    @SuppressLint("MissingPermission")
    private fun moveCameraToLastKnownLocation() {
        LocationServices.getFusedLocationProviderClient(requireActivity()).apply {
            lastLocation.addOnSuccessListener {
                it?.let { latLng ->
                    map.moveCameraToUser(
                        LatLng(latLng.latitude, latLng.longitude),
                        LAST_KNOWN_LOCATION_MAP_ZOOM,
                        ULTRA_FAST_CAMERA_ANIMATION_DURATION
                    )
                }
            }
            lastLocation.addOnFailureListener {
                map.moveCameraToUser(
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


    private fun checkLocationSetting():Task<LocationSettingsResponse>{
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(request)
            .setAlwaysShow(true)

        return LocationServices.getSettingsClient(requireContext())
            .checkLocationSettings(builder.build())

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

        lifecycleScope.launch(Dispatchers.Main) {
            map.deleteAllMapMarker()
            delay(100)
            map.getLocationSearchResult(requestCode, resultCode, data)
        }


    }

    private fun getGPSDialogSettingResult(requestCode: Int,resultCode: Int){
        when (requestCode) {
            REQUEST_CHECK_SETTING ->
                when (resultCode) {
                    RESULT_OK -> {
                        toggleStartButton()
                    }
                    RESULT_CANCELED->{

                    }
                }
        }
    }



    private fun addAllPolyLines() {
        outerPolyline.forEach {
            customPolylineAppearance().addAll(it).apply {
                map.getMap()?.addPolyline(this)
            }
        }
    }


    override fun onMapLongClick(point: LatLng): Boolean {


        lifecycleScope.launch(Dispatchers.Main){
            map.deleteAllMapMarker()
            delay(100)
            map.pointMapMarker(point)
        }
        normalBottomSheet.show()
        bottomNavigation.hide()
        return true
    }
    override fun onMapClick(point: LatLng): Boolean {
        map.deleteAllMapMarker()
        normalBottomSheet.hide()
        bottomNavigation.show()
        return true
    }




    private fun subscribeToObservers() {
        TrackingService().isCurrentlyTracking().observe(viewLifecycleOwner) {
            isTracking = it
            map.createLocationPuck()
            updateButtons()

        }

        TrackingService().outerPolyline().observe(viewLifecycleOwner) {
            outerPolyline = it

            addLatestPolyline()
            if (hasExistingInnerAndOuterPolyLines()) {
                map.moveCameraToUser(
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




    private fun addLatestPolyline() {
        if (hasExistingInnerPolyLines()) {
            val innerPolylinePosition = outerPolyline.last().size - 2
            val preLastLatLng = outerPolyline.last()[innerPolylinePosition]
            val lastLatLng = outerPolyline.last().last()

            customPolylineAppearance()
                .add(preLastLatLng)
                .add(lastLatLng).apply {
                    map.getMap()?.addPolyline(this)
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




    override fun onStart() {
        super.onStart();
        map.getMapView().onStart()
    }


    override fun onResume() {
        super.onResume();
        map.getMapView().onResume()
        registerReceiver()
    }


    override fun onPause() {
        super.onPause();
        map.getMapView().onPause()
    }


    override fun onStop() {
        super.onStop();
        map.getMapView().onStop()
        unregisterReceiver()
    }


    override fun onSaveInstanceState(outState:Bundle) {
        super.onSaveInstanceState(outState);
        map.getMapView().onSaveInstanceState(outState)
    }


    override fun onLowMemory() {
        super.onLowMemory();
        map.getMapView().onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        map.getMapView().onDestroy()
        unregisterReceiver()

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


    override fun onMove(detector: MoveGestureDetector) {
        locationFAB.changeFloatingButtonIconBlack()
    }

    override fun onMoveEnd(detector: MoveGestureDetector) {}


}