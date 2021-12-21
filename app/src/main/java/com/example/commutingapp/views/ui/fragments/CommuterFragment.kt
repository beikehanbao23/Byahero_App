package com.example.commutingapp.views.ui.fragments


import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.*
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
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
import com.example.commutingapp.utils.others.Constants.REQUEST_CODE_AUDIO_RECORD_PERMISSION
import com.example.commutingapp.utils.others.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.commutingapp.utils.others.Constants.REQUEST_CONTINUE_NAVIGATION
import com.example.commutingapp.utils.others.Constants.REQUEST_SEARCH_RESULT
import com.example.commutingapp.utils.others.Constants.REQUEST_USER_LOCATION
import com.example.commutingapp.utils.others.Constants.REQUEST_VOICE_COMMAND
import com.example.commutingapp.utils.others.Constants.TEN_METERS
import com.example.commutingapp.utils.others.Constants.ULTRA_FAST_CAMERA_ANIMATION_DURATION
import com.example.commutingapp.utils.others.FragmentToActivity
import com.example.commutingapp.utils.others.SwitchState
import com.example.commutingapp.utils.others.TrackingPermissionUtility.hasLocationPermission
import com.example.commutingapp.utils.others.TrackingPermissionUtility.hasRecordAudioPermission
import com.example.commutingapp.utils.others.TrackingPermissionUtility.requestPermission
import com.example.commutingapp.utils.others.TrackingPermissionUtility.requestRecordAudioPermission
import com.example.commutingapp.views.dialogs.CustomDialogBuilder
import com.example.commutingapp.views.dialogs.DialogDirector
import com.example.commutingapp.views.ui.subComponents.BottomNavigation
import com.example.commutingapp.views.ui.subComponents.Component
import com.example.commutingapp.views.ui.subComponents.StartingBottomSheet
import com.example.commutingapp.views.ui.subComponents.fab.LocationButton
import com.example.commutingapp.views.ui.subComponents.fab.MapTypes
import com.example.commutingapp.views.ui.subComponents.fab.mapDetails.Map3DBuilding
import com.example.commutingapp.views.ui.subComponents.fab.mapDetails.MapDetailsWrapper
import com.example.commutingapp.views.ui.subComponents.fab.mapDetails.MapTraffic
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
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin
import com.mapbox.mapboxsdk.plugins.traffic.TrafficPlugin
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import kotlin.reflect.KFunction0



class CommuterFragment : Fragment(R.layout.commuter_fragment), EasyPermissions.PermissionCallbacks,
     MapboxMap.OnMapLongClickListener, MapboxMap.OnMapClickListener,MapboxMap.OnMoveListener {



    private lateinit var dialogDirector: DialogDirector
    private var binding: CommuterFragmentBinding? = null
    private lateinit var searchLocationButton: AppCompatButton
    private lateinit var notifyListener: FragmentToActivity<Fragment>
    private lateinit var bottomSheet: Component
    private lateinit var bottomNavigation:Component
    private lateinit var locationFAB:LocationButton
    private lateinit var mapTypes:MapTypes
    private lateinit var map:MapWrapper<MapView>
    private var userDestinationLocation: LatLng? = null
    private lateinit var map3DBuilding: MapDetailsWrapper
    private lateinit var mapTraffic: MapDetailsWrapper
    private lateinit var traffic : TrafficPlugin
    private lateinit var building3D : BuildingPlugin

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
        map.setupUI(mapTypes.currentMapType())
        locationFAB.updateLocationFloatingButtonIcon()
        provideObservers()

    }


    @Suppress("Warnings")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            this.notifyListener = context as FragmentToActivity<Fragment>
        } catch (e: ClassCastException) { }
    }

    private fun openVoiceSearchCommand(){
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        startActivityForResult(intent,REQUEST_VOICE_COMMAND)
    }

    private fun provideObservers(){

        with(binding) {
            map.getPlaceName().observe(viewLifecycleOwner) {
                this?.progressBar?.visibility = View.GONE
                if (it == null) {
                    this?.geocodePlaceName?.visibility = View.GONE
                    return@observe
                }
                this?.geocodePlaceName?.visibility = View.VISIBLE
                this?.geocodePlaceName?.text = it

            }


            map.getPlaceText().observe(viewLifecycleOwner) {
                this?.progressBar?.visibility = View.GONE
                if (it == null) {
                    this?.geocodePlaceText?.visibility = View.GONE
                    return@observe
                }
                this?.geocodePlaceText?.visibility = View.VISIBLE
                this?.geocodePlaceText?.text = it
            }
        }
    }

    private fun initializeComponents(view: View) {
        dialogDirector = DialogDirector(requireActivity())
        searchLocationButton = view.findViewById(R.id.buttonLocationSearch)
        bottomSheet = Component(StartingBottomSheet(view,requireContext()))
        bottomNavigation = Component(BottomNavigation(notifyListener))
        locationFAB = LocationButton(binding!!,requireContext())
        mapTypes = MapTypes(requireContext())
        map3DBuilding = MapDetailsWrapper(Map3DBuilding(requireContext()))
        mapTraffic = MapDetailsWrapper(MapTraffic(requireContext()))


        val mapbox = object : MapBox(view,requireActivity()){

            override fun onMapTrafficInitialized(trafficPlugin: TrafficPlugin) {
                traffic = trafficPlugin
                showTrafficView()
            }

            override fun onMap3DBuildingInitialized(buildingPlugin: BuildingPlugin) {
                building3D = buildingPlugin
                show3DBuildingView()
            }

            override fun onMapReady(mapboxMap: MapboxMap) {
                mapboxMap.addOnMapLongClickListener(this@CommuterFragment)
                mapboxMap.addOnMapClickListener(this@CommuterFragment)
                mapboxMap.addOnMoveListener(this@CommuterFragment)
                moveCameraToLastKnownLocation()
            }
            override fun onSearchCompleted(intent: Intent) {
            startActivityForResult(intent,REQUEST_SEARCH_RESULT)
            }
        }

        map = MapWrapper(mapbox)


    }
    private fun showTrafficView(){
        traffic.setVisibility(mapTraffic.isButtonSelected())

    }
    private fun show3DBuildingView(){
        building3D.setVisibility(map3DBuilding.isButtonSelected())
    }
    private fun provideClickListeners() {
        provideMapTypeDialogListener()
        provideLocationButtonListener()
        provideStartButtonListener()
        provideDirectionButtonListener()
        provideSaveButtonListener()
        provideShareButtonListener()
        provideVoiceSpeechButtonListener()
    }
    private fun provideMapTypeDialogListener() {
        binding?.floatingActionButtonChooseMap?.setOnClickListener {
            dialogDirector.buildChooseMapTypeDialog().apply {

                mapTypes.setMapSelectedIndicator(this)
                provideMapDetailsButtonListenerOf(this, map3DBuilding, R.id.maps3dDetailsButton, ::show3DBuildingView)
                provideMapDetailsButtonListenerOf(this, mapTraffic, R.id.trafficMapDetailsButton, ::showTrafficView)
                setMapTypeListeners(this)
                show()
            }
        }
    }


    private fun provideMapDetailsButtonListenerOf(customDialogBuilder: CustomDialogBuilder, mapDetails: MapDetailsWrapper, id: Int, showViews: KFunction0<Unit>) {
        with(mapDetails) {
            customDialogBuilder.also { dialogBuilder ->
                addMapSelectedIndicator(dialogBuilder)
                dialogBuilder.findViewById<View>(id)?.setOnClickListener {
                    if (this.isButtonSelected()) {
                        changeMapButtonState(SwitchState.OFF)
                    } else {
                        changeMapButtonState(SwitchState.ON)
                    }
                    showViews()
                    addMapSelectedIndicator(dialogBuilder)
                }
            }
        }
    }


    private fun provideLocationButtonListener() {
        binding?.floatingActionButtonLocation?.setOnClickListener {
            if (hasLocationPermission(requireContext())) {
                renderUserLocation()
            }else{
                requestPermission(this)
            }
        }
    }
    private fun renderUserLocation(){
        checkLocationSetting().addOnCompleteListener {task->
            if(!Connection.hasGPSConnection(requireContext())) {
                askGPS(task, REQUEST_USER_LOCATION)
            }else{
                displayUserLocation()
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
            if (hasLocationPermission(requireContext())) {
                if(!Connection.hasInternetConnection(requireContext())){
                    DialogDirector(requireActivity()).buildNoInternetDialog()
                    bottomSheet.hide()
                    return@setOnClickListener
                }
                bottomSheet.hide()
                bottomNavigation.hide()
                locationFAB.hideLocationFloatingButton()

                checkLocationSetting().addOnCompleteListener {task->
                    onStartButtonClickAskGPS(task)
             }
          }else{
                requestPermission(this)
          }
       }
    }

    private fun onStartButtonClickAskGPS(task:Task<LocationSettingsResponse>){
        if(!Connection.hasGPSConnection(requireContext())) {
            askGPS(task,REQUEST_CONTINUE_NAVIGATION)
        }else{
            map.createLocationPuck()
            showNavigation()
        }
    }
    private fun askGPS(task:Task<LocationSettingsResponse>,requestCode: Int){

        try {
            task.getResult(ApiException::class.java)
        } catch (e: ApiException) {
            handleLocationResultException(e, requestCode)
        }

    }
    private fun showNavigation(){
        userDestinationLocation?.let { destinationLocation ->
            map.getLastKnownLocation()?.let { lastLocation ->
            val action = CommuterFragmentDirections.commuterFragmentToNavigationFragment(destinationLocation,lastLocation)
                Navigation.findNavController(binding!!.root).navigate(action)
            }
        }


    }

    private fun provideDirectionButtonListener(){
        binding?.directionsButton?.setOnClickListener {

            if(!Connection.hasInternetConnection(requireContext())){
                DialogDirector(requireActivity()).buildNoInternetDialog()
                bottomSheet.hide()
                return@setOnClickListener
            }
                map.createDirections()
                binding?.directionsButton?.visibility = View.GONE
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
    private fun provideVoiceSpeechButtonListener(){
        binding?.voiceSpeechButton?.setOnClickListener {
            if(hasRecordAudioPermission(requireContext())){
                openVoiceSearchCommand()
            }else{
                requestRecordAudioPermission(this)
            }

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
        }catch (e:IllegalArgumentException){
            Timber.e("Register Receiver: ${e.message}")
        }
    }
    private fun unregisterReceiver(){
        try {
        requireActivity().unregisterReceiver(locationSwitchStateReceiver())
        }catch (e:IllegalArgumentException){
            Timber.e("Unregister Receiver: ${e.message}")
        }
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
            mapTypes.getMapTypeButtons().forEach { hashMap->
               mapTypeListener.findViewById<View>(hashMap.value )?.setOnClickListener {
                   val currentType = hashMap.key
                   if(previousType != currentType) {
                       mapTypes.changeMapType(mapTypeListener, currentType)
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
    private fun handleLocationResultException(e: ApiException, requestCode: Int) {
        when (e.statusCode) {
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                (e as ResolvableApiException).apply {
                startIntentSenderForResult(this.resolution.intentSender, requestCode, null, 0, 0, 0, null)
                } }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        getOnActivityResult(requestCode, resultCode, data)
    }


    private fun resetBottomSheetPlace(){
        lifecycleScope.launch {
            bottomNavigation.hide()
            showPlaceResult()
            delay(50)
            bottomSheet.show()
        }
    }
    private fun showPlaceResult(){

            if(!Connection.hasInternetConnection(requireContext())){
                hidePlace()
                return
            }
            showPlace()
    }
    private fun showPlace(){
        with(binding){
            this?.progressBar?.visibility = View.VISIBLE
            this?.geocodePlaceName?.text = ""
            this?.geocodePlaceText?.text = ""
        }
    }
    private fun hidePlace() {
        with(binding) {
            this?.progressBar?.visibility = View.GONE
            this?.geocodePlaceText?.visibility = View.GONE
            this?.geocodePlaceName?.visibility = View.GONE
        }
    }
    private fun getOnActivityResult(requestCode: Int, resultCode: Int, data:Intent?){
        when (requestCode) {
            REQUEST_USER_LOCATION  -> continueRequestUserLocation(resultCode)
            REQUEST_CONTINUE_NAVIGATION -> continueUserNavigation(resultCode)
            REQUEST_SEARCH_RESULT -> showSearchLocationResult(resultCode,data)
            REQUEST_VOICE_COMMAND -> showVoiceCommandSearchResult(resultCode, data)
        }
    }


    private fun showVoiceCommandSearchResult(resultCode: Int, data: Intent?){
        if(resultCode == RESULT_OK){
            data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.run {
               val voice = get(0)
               map.setVoiceSearchResult(voice)
               resetBottomSheetPlace()
            }
        }
    }

    private fun showSearchLocationResult(resultCode: Int, data: Intent?){
        if (resultCode == RESULT_OK) {
            map.locationSearchResult(data)
            resetBottomSheetPlace()
        }
    }

    private fun continueRequestUserLocation(resultCode: Int){
        if(resultCode == RESULT_OK){
            displayUserLocation()
        }
    }
    private fun continueUserNavigation(resultCode: Int){
        if(resultCode == RESULT_OK){
            showNavigation()
        }
    }
    override fun onMapLongClick(point: LatLng): Boolean {
        userDestinationLocation = point
        map.pointMapMarker(point)
        resetBottomSheetPlace()
        return true
    }
    override fun onMapClick(point: LatLng): Boolean {
        map.deleteRouteAndMarkers()
        bottomSheet.hide()
        bottomNavigation.show()
        binding?.directionsButton?.visibility = View.VISIBLE
        return true
    }

    override fun onStart() {
        super.onStart()
        map.getMapView().onStart()
        displayUserLocation()

    }
    override fun onResume() {
        super.onResume()
        map.getMapView().onResume()
        registerReceiver()
    }
    override fun onPause() {
        super.onPause()
        map.getMapView().onPause()
    }
    override fun onStop() {
        super.onStop()
        map.getMapView().onStop()
        unregisterReceiver()

    }
    override fun onSaveInstanceState(outState:Bundle) {
        super.onSaveInstanceState(outState)
        map.getMapView().onSaveInstanceState(outState)
    }
    override fun onLowMemory() {
        super.onLowMemory()
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
    Timber.e("onPermissionsDenied requestCode is $requestCode")
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        @Suppress("DEPRECATION")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION){
            renderUserLocation()
            return
        }
        if(requestCode == REQUEST_CODE_AUDIO_RECORD_PERMISSION){
            openVoiceSearchCommand()
        }

    }
    override fun onMoveBegin(detector: MoveGestureDetector){}
    override fun onMove(detector: MoveGestureDetector) {
        locationFAB.changeFloatingButtonIconBlack()
    }
    override fun onMoveEnd(detector: MoveGestureDetector) {}



}