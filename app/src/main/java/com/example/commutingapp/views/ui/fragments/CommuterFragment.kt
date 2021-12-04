package com.example.commutingapp.views.ui.fragments


import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.*
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.commutingapp.R
import com.example.commutingapp.databinding.CommuterFragmentBinding
import com.example.commutingapp.utils.InternetConnection.Connection
import com.example.commutingapp.utils.others.Constants
import com.example.commutingapp.utils.others.Constants.CAMERA_ZOOM_MAP_MARKER
import com.example.commutingapp.utils.others.Constants.DEFAULT_CAMERA_ANIMATION_DURATION
import com.example.commutingapp.utils.others.Constants.DEFAULT_LATITUDE
import com.example.commutingapp.utils.others.Constants.DEFAULT_LONGITUDE
import com.example.commutingapp.utils.others.Constants.DEFAULT_MAP_ZOOM
import com.example.commutingapp.utils.others.Constants.FAST_CAMERA_ANIMATION_DURATION
import com.example.commutingapp.utils.others.Constants.LAST_KNOWN_LOCATION_MAP_ZOOM
import com.example.commutingapp.utils.others.Constants.REQUEST_CHECK_SETTING
import com.example.commutingapp.utils.others.Constants.REQUEST_CODE_AUTOCOMPLETE
import com.example.commutingapp.utils.others.Constants.TEN_METERS
import com.example.commutingapp.utils.others.Constants.ULTRA_FAST_CAMERA_ANIMATION_DURATION
import com.example.commutingapp.utils.others.FragmentToActivity
import com.example.commutingapp.utils.others.TrackingPermissionUtility.hasLocationPermission
import com.example.commutingapp.utils.others.TrackingPermissionUtility.requestPermission
import com.example.commutingapp.viewmodels.MainViewModel
import com.example.commutingapp.views.dialogs.CustomDialogBuilder
import com.example.commutingapp.views.dialogs.DialogDirector
import com.example.commutingapp.views.ui.subComponents.BottomNavigation
import com.example.commutingapp.views.ui.subComponents.Component
import com.example.commutingapp.views.ui.subComponents.StartingBottomSheet
import com.example.commutingapp.views.ui.subComponents.fab.LocationButton
import com.example.commutingapp.views.ui.subComponents.fab.MapTypes
import com.example.commutingapp.views.ui.subComponents.maps.MapWrapper
import com.example.commutingapp.views.ui.subComponents.maps.mapBox.MapBox
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@AndroidEntryPoint
class CommuterFragment : Fragment(R.layout.commuter_fragment), EasyPermissions.PermissionCallbacks,
     MapboxMap.OnMapLongClickListener, MapboxMap.OnMapClickListener,MapboxMap.OnMoveListener {


    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var dialogDirector: DialogDirector
    private var binding: CommuterFragmentBinding? = null
    private lateinit var searchLocationButton: AppCompatButton
    private lateinit var notifyListener: FragmentToActivity<Fragment>
    private lateinit var bottomSheet: Component
    private lateinit var bottomNavigation:Component
    private lateinit var locationFAB:LocationButton
    private lateinit var mapTypesFAB:MapTypes
    private lateinit var map:MapWrapper<MapView>
    private var latLng: LatLng? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = CommuterFragmentBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeComponents(view)
        bottomSheet.hide()
        bottomNavigation.show()
        provideClickListeners()
        map.getMapView().apply {
            onCreate(savedInstanceState)
            isClickable = true
        }
        map.setupUI(mapTypesFAB.loadMapType())
        locationFAB.updateLocationFloatingButtonIcon()
        provideObservers()


    }
    private fun provideObservers(){
        map.getPlaceName().observe(viewLifecycleOwner){
            binding?.progressBar?.visibility = View.GONE
            binding?.geocodePlaceName?.text = it
        }
        map.getPlaceText().observe(viewLifecycleOwner){
            binding?.progressBar?.visibility = View.GONE
            binding?.geocodePlaceText?.text = it

        }
    }
    private fun initializeComponents(view: View) {
        dialogDirector = DialogDirector(requireActivity())
        searchLocationButton = view.findViewById(R.id.buttonLocationSearch)
        bottomSheet = Component(StartingBottomSheet(view,requireContext()))
        bottomNavigation = Component(BottomNavigation(notifyListener))
        locationFAB = LocationButton(binding!!,requireContext())
        mapTypesFAB = MapTypes(requireContext())


        val mapbox = object : MapBox(view,requireActivity()){

            override fun onMapReady(mapboxMap: MapboxMap) {
                mapboxMap.addOnMapLongClickListener(this@CommuterFragment)
                mapboxMap.addOnMapClickListener(this@CommuterFragment)
                mapboxMap.addOnMoveListener(this@CommuterFragment)
                moveCameraToLastKnownLocation()
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
        binding?.floatingActionButtonChooseMap?.setOnClickListener {
            dialogDirector.constructChooseMapDialog().apply {
                mapTypesFAB.setMapSelectedIndicator(this)
                setMapTypeListeners(this)
                show()
            } }
    }
    private fun provideLocationButtonListener() {
        binding?.floatingActionButtonLocation?.setOnClickListener {
            if (requestPermissionGranted()) {
                checkLocationSetting().addOnCompleteListener {
                    try {
                        it.getResult(ApiException::class.java)
                        displayUserLocation()
                    } catch (e: ApiException) {
                        handleLocationResultException(e)
                    }
                }
            }
        }
    }
    private fun displayUserLocation(){
        with(map) {
            getLastKnownLocation()?.let { location ->
                createLocationPuck()
                moveCameraToUser(location, CAMERA_ZOOM_MAP_MARKER, FAST_CAMERA_ANIMATION_DURATION)
                locationFAB.changeFloatingButtonIconBlue()
            }
        }

    }
    private fun provideStartButtonListener(){
        binding?.startButton?.setOnClickListener {
            if (requestPermissionGranted()) {
                bottomSheet.hide()
                bottomNavigation.hide()
                locationFAB.hideLocationFloatingButton()
                checkLocationSetting().addOnCompleteListener {
                try{
                    it.getResult(ApiException::class.java)
                    map.createLocationPuck()
                    showNavigation()
                }catch (e:ApiException){
                    handleLocationResultException(e)
                }
             }
          }
       }
    }
    private fun showNavigation(){
        latLng?.let { destinationLocation ->
            map.getLastKnownLocation()?.let { lastLocation ->
                notifyListener.onThirdNotify(NavigationFragment(), destinationLocation,lastLocation)
            }
        }
    }

    private fun provideDirectionButtonListener(){
        binding?.directionsButton?.setOnClickListener {
            map.createDirections()
        }
    }
    private fun provideSaveButtonListener() {
        binding?.saveButton?.setOnClickListener {
        }
    }
    private fun provideShareButtonListener(){
        binding?.shareButton?.setOnClickListener {

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

        var previousType = ""
        customDialogBuilder.also { mapTypeListener->
            mapTypesFAB.getMapTypeButtons().forEach { hashMap->
               mapTypeListener.findViewById<View>(hashMap.value )?.setOnClickListener {
                   val currentType = hashMap.key
                   if(previousType != currentType) {
                       mapTypesFAB.changeMapType(mapTypeListener, currentType)
                       map.updateMapStyle(currentType)
                       previousType = currentType
                   }
               }
           }
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
    private fun checkLocationSetting():Task<LocationSettingsResponse>{

        return LocationServices.getSettingsClient(requireContext())
            .checkLocationSettings(LocationSettingsRequest.Builder()
                .addLocationRequest(request)
                .setAlwaysShow(true)
                .build())

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
        map.getLocationSearchResult(requestCode, resultCode, data)
        resetBottomSheetPlace()
    }

    private fun resetBottomSheetPlace(){
        lifecycleScope.launch {
            bottomNavigation.hide()
            displayBottomSheetResults()
            delay(150)
            bottomSheet.show()
        }
    }
    private fun displayBottomSheetResults(){
        with(binding){
            if(!Connection.hasInternetConnection(requireContext())){
                this?.progressBar?.visibility = View.GONE
                this?.geocodePlaceText?.visibility = View.GONE
                this?.geocodePlaceName?.visibility = View.GONE
            }else{
                this?.progressBar?.visibility = View.VISIBLE
                this?.geocodePlaceName?.text = ""
                this?.geocodePlaceText?.text = ""
            }
        }

    }
    private fun getGPSDialogSettingResult(requestCode: Int,resultCode: Int){
        when (requestCode) {
            REQUEST_CHECK_SETTING ->
                when (resultCode) {
                    RESULT_OK -> {
                        displayUserLocation()
                    }
                    RESULT_CANCELED->{

                    }
                }
        }
    }
    override fun onMapLongClick(point: LatLng): Boolean {
        latLng = point
        map.pointMapMarker(point)
        resetBottomSheetPlace()
        return true
    }
    override fun onMapClick(point: LatLng): Boolean {
        map.deleteRouteAndMarkers()
        bottomSheet.hide()
        bottomNavigation.show()
        return true
    }
    private fun requestPermissionGranted(): Boolean {
        if (hasLocationPermission(requireContext())) {
            return true
        }
        requestPermission(this)
        return false

    }
    @Suppress("Warnings")
    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            this.notifyListener = context as FragmentToActivity<Fragment>
        } catch (e: ClassCastException) { }
    }
    override fun onStart() {
        super.onStart();
        map.getMapView().onStart()
        displayUserLocation()
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
        map.clearCache()
        binding = null
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
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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