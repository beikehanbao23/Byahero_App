package com.example.commutingapp.views.ui.fragments


import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.*
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.commutingapp.BuildConfig
import com.example.commutingapp.R
import com.example.commutingapp.databinding.FragmentCommuterBinding
import com.example.commutingapp.feature_note.domain.model.PlaceBookmarks
import com.example.commutingapp.feature_note.presentation.place.components.PlaceBookmarksEvent
import com.example.commutingapp.feature_note.presentation.place.components.PlaceBookmarksUiEvent
import com.example.commutingapp.feature_note.presentation.place.components.PlaceBookmarksViewModel
import com.example.commutingapp.utils.InternetConnection.Connection
import com.example.commutingapp.utils.others.Constants
import com.example.commutingapp.utils.others.Constants.CAMERA_ZOOM_MAP_MARKER
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
import com.example.commutingapp.utils.others.LastLocation
import com.example.commutingapp.utils.others.SwitchState
import com.example.commutingapp.utils.others.TrackingPermissionUtility.hasLocationPermission
import com.example.commutingapp.utils.others.TrackingPermissionUtility.hasRecordAudioPermission
import com.example.commutingapp.utils.others.TrackingPermissionUtility.requestLocationPermission
import com.example.commutingapp.utils.others.TrackingPermissionUtility.requestRecordAudioPermission
import com.example.commutingapp.views.dialogs.CustomDialogBuilder
import com.example.commutingapp.views.dialogs.DialogDirector
import com.example.commutingapp.views.ui.subComponents.maps.MapImpl
import com.example.commutingapp.views.ui.subComponents.maps.mapBox.MapBox
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin
import com.mapbox.mapboxsdk.plugins.traffic.TrafficPlugin
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap


@AndroidEntryPoint
class CommuterFragment : Fragment(R.layout.fragment_commuter), EasyPermissions.PermissionCallbacks,
     MapboxMap.OnMapLongClickListener, MapboxMap.OnMapClickListener,MapboxMap.OnMoveListener {


    private val viewModel: PlaceBookmarksViewModel by viewModels()
    private val commuterArgs: CommuterFragmentArgs by navArgs()
    private lateinit var dialogDirector: DialogDirector
    private var commuterBinding: FragmentCommuterBinding? = null
    private lateinit var fragmentToActivityListener: FragmentToActivity<Fragment>



    private lateinit var map:MapImpl<MapView>
    private var userDestinationLocation: LatLng? = null
    private lateinit var traffic : TrafficPlugin
    private lateinit var building3D : BuildingPlugin
    private var isOpenedFromBookmarks:Boolean = false
    private lateinit var commuterBottomSheet: BottomSheetBehavior<View>
    private lateinit var map3dDetailsPreferences: SharedPreferences
    private lateinit var mapTrafficDetailsPreferences: SharedPreferences
    private lateinit var mapTypesPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        commuterBinding = FragmentCommuterBinding.inflate(inflater,container,false)
        return commuterBinding!!.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeComponents(view)
        hidePlacesBottomSheet()
        showBottomNavigation()
        provideClickListeners()
        map.getMapView().apply {
            onCreate(savedInstanceState)
            isClickable = true
        }
        map.setupUI(currentMapType())
        updateLocationFloatingButtonIcon()
        provideObservers()
        isOpenedFromBookmarks = commuterArgs.isOpenFromBookmarks

        markLocationFromBookmarks()
        collectLifecycleFlowUiEvent()


    }
    private fun isOpenedFromBookmarksRVPlaceItem() = isOpenedFromBookmarks && commuterArgs.bookmarkSelectedLocation != null
    private fun isOpenedFromBookmarksAddButton() = isOpenedFromBookmarks && commuterArgs.bookmarkSelectedLocation == null

    private fun markLocationFromBookmarks(){
        if(isOpenedFromBookmarksRVPlaceItem()) {
            val location = LatLng(
                commuterArgs.bookmarkSelectedLocation!!.latitude,
                commuterArgs.bookmarkSelectedLocation!!.longitude)
            map.pointMapMarker(location)
            resetBottomSheetPlace()
        }
    }
    private fun collectLifecycleFlowUiEvent(){
        collectLifecycleFlow(viewModel.event){
            when(it){
                is PlaceBookmarksUiEvent.SavePlace-> {
                    Toast.makeText(requireContext(),"Place Successfully Saved!",Toast.LENGTH_SHORT).show()
                }
                is PlaceBookmarksUiEvent.ShowSnackBar->{
                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun <T> Fragment.collectLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                flow.collect(collect)
            }
        }
    }

    @Suppress("Warnings")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            this.fragmentToActivityListener = context as FragmentToActivity<Fragment>
            map3dDetailsPreferences = context.getSharedPreferences(Constants.KEY_MAPS_3D,Context.MODE_PRIVATE)
            mapTrafficDetailsPreferences = context.getSharedPreferences(Constants.KEY_MAPS_TRAFFIC,Context.MODE_PRIVATE)
            mapTypesPreferences =  context.getSharedPreferences(Constants.KEY_MAPS_TYPE, Context.MODE_PRIVATE)
        } catch (e: ClassCastException) { }
    }

    private fun openVoiceSearchCommand(){
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        startActivityForResult(intent,REQUEST_VOICE_COMMAND)
    }

    private fun provideObservers(){

        with(commuterBinding) {

            map.getPlaceLocation().observe(viewLifecycleOwner){
                userDestinationLocation = it
            }

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

    private fun openSearchbarFromBookmarks(){
        if(isOpenedFromBookmarksAddButton()){
            val intent = map.getLocationSearchIntent()
            startActivityForResult(intent,REQUEST_SEARCH_RESULT)
            isOpenedFromBookmarks = false
        }
    }


    private fun initializeComponents(view: View) {
        dialogDirector = DialogDirector(requireActivity())
        commuterBottomSheet = BottomSheetBehavior.from(view.findViewById(R.id.bottomSheetNormalState)).apply {
            this.isHideable = true
        }




        val mapbox = object : MapBox(view,requireActivity()){
            override fun onMapSymbolsInit() {
                openSearchbarFromBookmarks()
            }

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

        }

        map = MapImpl(mapbox)


    }

     private fun hidePlacesBottomSheet(){
        commuterBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }
     private fun showPlacesBottomSheet() {
        renderBottomSheetButtons()
        commuterBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }







    private fun renderBottomSheetButtons(){
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
        commuterBinding?.saveButton?.visibility = View.GONE
        commuterBinding?.directionsButton?.visibility = View.VISIBLE
        commuterBinding?.startButton?.visibility = View.VISIBLE
    }
    private fun showHasInternetAndNoGpsBottomSheetLayout() {
        commuterBinding?.startButton?.visibility = View.GONE
        commuterBinding?.saveButton?.visibility = View.VISIBLE
        commuterBinding?.directionsButton?.visibility = View.VISIBLE
    }
    private fun showNoInternetAndNoGpsBottomSheetLayout() {
        commuterBinding?.startButton?.visibility = View.GONE
        commuterBinding?.saveButton?.visibility = View.GONE
        commuterBinding?.directionsButton?.visibility = View.VISIBLE
    }
    private fun showDefaultBottomSheetLayout() {
        commuterBinding?.saveButton?.visibility = View.VISIBLE
        commuterBinding?.directionsButton?.visibility = View.VISIBLE
        commuterBinding?.startButton?.visibility = View.VISIBLE

    }

    private fun showBottomNavigation() {
        fragmentToActivityListener.onSecondNotify()
    }

    private fun hideBottomNavigation() {
        fragmentToActivityListener.onFirstNotify()
    }

    private fun showTrafficView(){
        traffic.setVisibility(isMapTrafficButtonSelected())

    }
    private fun show3DBuildingView(){
        building3D.setVisibility(isMap3dButtonSelected())
    }
    private fun provideClickListeners() {
        provideMapTypeDialogClickListener()
        provideLocationButtonClickListener()
        provideStartButtonClickListener()
        provideDirectionButtonClickListener()
        provideSaveButtonClickListener()
        provideSpeechButtonClickListener()
        provideSearchButtonClickListener()

    }

    private fun provideSearchButtonClickListener(){
        commuterBinding?.buttonLocationSearch?.setOnClickListener {
            val intent = map.getLocationSearchIntent()
            startActivityForResult(intent,REQUEST_SEARCH_RESULT)
        }
    }




    private fun setMapSelectedIndicator(customDialogBuilder: CustomDialogBuilder){
        customDialogBuilder.also {
            val mapTypeButton = getMapTypeButtons().getValue(currentMapType())
            it.findViewById<View>(mapTypeButton)?.setBackgroundResource(R.drawable.map_type_selected_state_indicator)
        }
    }

    private fun removePreviousMapTypeIndicator(customDialogBuilder: CustomDialogBuilder){
        customDialogBuilder.apply {
            for(i in getMapTypeButtons()){
                if(i.key == currentMapType()){
                    continue
                }else{
                    findViewById<View>(i.value)?.setBackgroundColor(Color.WHITE)
                }
            }
        }
    }

    private fun getMapTypeButtons():HashMap<String,Int> =
        HashMap<String,Int>().apply {
            this[Style.LIGHT] = R.id.defaultMapStyleButton
            this[Style.DARK] = R.id.darkMapStyleButton
            this[Style.MAPBOX_STREETS] = R.id.streetMapStyleButton
            this[Style.SATELLITE_STREETS] = R.id.satelliteStreetsMapStyleButton
            this[BuildConfig.MAP_STYLE] = R.id.neonMapStyleButton
            this[Style.SATELLITE] = R.id.satelliteMapStyleButton

        }



    private fun changeMapType(customDialogBuilder: CustomDialogBuilder,mapType: String) {
        saveMapTypeToSharedPreference(mapType)
        removePreviousMapTypeIndicator(customDialogBuilder)
        setMapSelectedIndicator(customDialogBuilder)
    }

    private fun currentMapType():String = mapTypesPreferences.getString(Constants.KEY_MAPS_TYPE, Style.LIGHT).toString()

    private fun saveMapTypeToSharedPreference(mapType: String) {
        mapTypesPreferences.edit().putString(Constants.KEY_MAPS_TYPE, mapType).apply()
    }



    private fun addMap3dButtonSelectedIndicator(customDialogBuilder: CustomDialogBuilder) {
        customDialogBuilder.apply {
            if (isMap3dButtonSelected()) {
                findViewById<View>(R.id.maps3dDetailsButton)
                    ?.setBackgroundResource(R.drawable.map_type_selected_state_indicator)
            } else {
                findViewById<View>(R.id.maps3dDetailsButton)?.setBackgroundColor(Color.WHITE)
            }
        }
    }

    private fun changeMap3dButtonState(state: SwitchState) {
        map3dDetailsPreferences.edit().putString(Constants.KEY_MAPS_3D,state.toString()).apply()
    }


    private fun isMap3dButtonSelected() =
        map3dDetailsPreferences.getString(Constants.KEY_MAPS_3D,
            SwitchState.OFF.toString()) == SwitchState.ON.toString()







    private fun addMapTrafficButtonSelectedIndicator(customDialogBuilder: CustomDialogBuilder) {
        customDialogBuilder.apply {
            if(isMapTrafficButtonSelected()){
                findViewById<View>( R.id.trafficMapDetailsButton)?.setBackgroundResource(R.drawable.map_type_selected_state_indicator)
            }else{
                findViewById<View>( R.id.trafficMapDetailsButton)?.setBackgroundColor(Color.WHITE)
            }
        }
    }


    private fun changeMapTrafficButtonState(state: SwitchState) {
        mapTrafficDetailsPreferences.edit().putString(Constants.KEY_MAPS_TRAFFIC,state.toString()).apply()
    }

    private fun isMapTrafficButtonSelected() =
        mapTrafficDetailsPreferences.getString(Constants.KEY_MAPS_TRAFFIC,SwitchState.OFF.toString()) == SwitchState.ON.toString()






    private fun provideMapTypeDialogClickListener() {
        commuterBinding?.floatingActionButtonChooseMap?.setOnClickListener {
            dialogDirector.buildMapTypeDialog().apply {

                setMapSelectedIndicator(this)

                addMapTrafficButtonSelectedIndicator(this)
                addMap3dButtonSelectedIndicator(this)


                setMapTypeClickListeners(this)
                show()
            }
        }
    }


    private fun setMapTypeClickListeners(customDialogBuilder: CustomDialogBuilder) {

        var previousType = ""
        customDialogBuilder.also { mapTypeListener->
            getMapTypeButtons().forEach { hashMap->


                mapTypeListener.findViewById<View>(hashMap.value )?.setOnClickListener {
                    val currentType = hashMap.key
                    if(previousType != currentType) {
                        changeMapType(mapTypeListener, currentType)
                        map.updateMapStyle(currentType)
                        previousType = currentType
                    }
                }

                mapTypeListener.findViewById<View>(R.id.maps3dDetailsButton)?.setOnClickListener {
                    if(isMap3dButtonSelected()){
                        changeMap3dButtonState(SwitchState.OFF)
                    }else{
                        changeMap3dButtonState(SwitchState.ON)
                    }
                    show3DBuildingView()
                    addMap3dButtonSelectedIndicator(customDialogBuilder)
                }


                mapTypeListener.findViewById<View>(R.id.trafficMapDetailsButton)?.setOnClickListener {
                    if(isMapTrafficButtonSelected()){
                        changeMapTrafficButtonState(SwitchState.OFF)
                    }else{
                        changeMapTrafficButtonState(SwitchState.ON)
                    }
                    showTrafficView()
                    addMapTrafficButtonSelectedIndicator(customDialogBuilder)
                }


            }
        }
    }


    private fun showLocationFloatingButton(){
        commuterBinding!!.floatingActionButtonLocation.visibility = View.VISIBLE
    }
    private fun hideLocationFloatingButton(){
        commuterBinding!!.floatingActionButtonLocation.visibility = View.GONE
    }

    private fun updateLocationFloatingButtonIcon()=
        if(Connection.hasGPSConnection(requireContext())) changeFloatingButtonIconBlack() else changeFloatingButtonIconRed()

    private fun changeFloatingButtonIconRed(){
        changeLocationFloatingButtonIconColor(Color.RED)
        changeLocationFloatingButtonIcon(R.drawable.ic_location_asking)
    }
    private fun changeFloatingButtonIconBlack(){
       changeLocationFloatingButtonIconColor(Color.BLACK)
        changeLocationFloatingButtonIcon(R.drawable.ic_baseline_my_location)
    }
    private fun changeFloatingButtonIconBlue(){
        changeLocationFloatingButtonIconColor(Color.BLUE)
        changeLocationFloatingButtonIcon(R.drawable.ic_baseline_my_location)
    }
    private fun changeLocationFloatingButtonIconColor(@ColorInt color:Int){
        commuterBinding?.floatingActionButtonLocation?.let {
            ImageViewCompat.setImageTintList(
                it,
                ColorStateList.valueOf(color))
        }

    }
    private fun changeLocationFloatingButtonIcon(@DrawableRes imageId:Int){
        commuterBinding?.floatingActionButtonLocation?.setImageResource(imageId)

    }

    private fun provideLocationButtonClickListener() {
        commuterBinding?.floatingActionButtonLocation?.setOnClickListener {
            if (hasLocationPermission(requireContext())) {
                renderUserLocation()
            }else{
                requestLocationPermission(this)
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
                changeFloatingButtonIconBlue()
            }
        }

    }
    private fun provideStartButtonClickListener(){
        commuterBinding?.startButton?.setOnClickListener {
            if (hasLocationPermission(requireContext())) {
                if(!Connection.hasInternetConnection(requireContext())){
                    DialogDirector(requireActivity()).buildNoInternetDialog()
                    hidePlacesBottomSheet()
                    return@setOnClickListener
                }
                hidePlacesBottomSheet()
                hideBottomNavigation()
                hideLocationFloatingButton()

                checkLocationSetting().addOnCompleteListener {task->
                    onStartButtonClickAskGPS(task)
             }
          }else{
                requestLocationPermission(this)
          }
       }
    }

    private fun onStartButtonClickAskGPS(task:Task<LocationSettingsResponse>){
        if(!Connection.hasGPSConnection(requireContext())) {
            askGPS(task,REQUEST_CONTINUE_NAVIGATION)
        }else{
            map.createLocationPuck()
            showNavigationFragment()
        }
    }
    private fun askGPS(task:Task<LocationSettingsResponse>,requestCode: Int){

        try {
            task.getResult(ApiException::class.java)
        } catch (e: ApiException) {
            handleLocationResultException(e, requestCode)
        }

    }
    private fun showNavigationFragment(){
        userDestinationLocation?.let { destinationLocation ->
            map.getLastKnownLocation()?.let { lastLocation ->
            val action = CommuterFragmentDirections.commuterFragmentToNavigationFragment(destinationLocation,lastLocation)
                Navigation.findNavController(commuterBinding!!.root).navigate(action)
            }
        }


    }



    private fun provideDirectionButtonClickListener(){
        commuterBinding?.directionsButton?.setOnClickListener {

            if(!Connection.hasInternetConnection(requireContext())){
                DialogDirector(requireActivity()).buildNoInternetDialog()
                hidePlacesBottomSheet()
                return@setOnClickListener
            }
                map.createDirections()
                commuterBinding?.directionsButton?.visibility = View.GONE
            }
        }

    private fun provideSaveButtonClickListener() {
        commuterBinding?.saveButton?.setOnClickListener {
            viewModel.onEvent(PlaceBookmarksEvent.SavePlace(
                PlaceBookmarks(
                    placeName = commuterBinding!!.geocodePlaceName.text as String,
                    placeText = commuterBinding!!.geocodePlaceText.text as String,
                    longitude = userDestinationLocation!!.longitude,
                    latitude = userDestinationLocation!!.latitude
                )
            ))
        }



    }

    private fun provideSpeechButtonClickListener(){
        commuterBinding?.voiceSpeechButton?.setOnClickListener {
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
        }catch (e:Exception){
            Timber.e("Register Receiver: ${e.message}")
        }
    }
    private fun unregisterReceiver(){
        try {
        requireActivity().unregisterReceiver(locationSwitchStateReceiver())
        }catch (e:Exception){
            Timber.e("Unregister Receiver: ${e.message}")
        }
    }
    private fun locationSwitchStateReceiver()= object: BroadcastReceiver(){

            override fun onReceive(context: Context?, intent: Intent?) {
                if(LocationManager.PROVIDERS_CHANGED_ACTION == intent?.action){
                   updateLocationFloatingButtonIcon()
                }
            }

    }



    @SuppressLint("MissingPermission")
    private fun moveCameraToLastKnownLocation() {
        if(hasLocationPermission(requireContext())){
            moveCameraToUser()
            return
        }
            requestLocationPermission(this)
    }

    private fun moveCameraToUser(){
        val listOfUserLocation = LastLocation.getUserLocation(requireContext())
        if(listOfUserLocation.isNotEmpty()){
            listOfUserLocation.forEach{ address ->
                map.moveCameraToUser(LatLng(address.latitude, address.longitude), LAST_KNOWN_LOCATION_MAP_ZOOM, ULTRA_FAST_CAMERA_ANIMATION_DURATION)
            }
            return
        }
        map.moveCameraToUser(LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE), DEFAULT_MAP_ZOOM, ULTRA_FAST_CAMERA_ANIMATION_DURATION)
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
        when (requestCode) {
            REQUEST_USER_LOCATION  -> continueRequestUserLocation(resultCode)
            REQUEST_CONTINUE_NAVIGATION -> continueUserNavigation(resultCode)
            REQUEST_SEARCH_RESULT -> showSearchLocationResult(resultCode,data)
            REQUEST_VOICE_COMMAND -> showVoiceCommandSearchResult(resultCode, data)
        }
    }


    private fun resetBottomSheetPlace(){
        lifecycleScope.launch {
            hideBottomNavigation()
            resetPlaceResult()
            delay(50)
            showPlacesBottomSheet()
        }
    }
    private fun resetPlaceResult(){

            if(!Connection.hasInternetConnection(requireContext())){
                hidePlace()
                return
            }
            showPlace()
    }
    private fun showPlace(){
        with(commuterBinding){
            this?.progressBar?.visibility = View.VISIBLE
            this?.geocodePlaceName?.text = ""
            this?.geocodePlaceText?.text = ""
        }
    }
    private fun hidePlace() {
        with(commuterBinding) {
            this?.progressBar?.visibility = View.GONE
            this?.geocodePlaceText?.visibility = View.GONE
            this?.geocodePlaceName?.visibility = View.GONE
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
            showNavigationFragment()
        }
    }
    override fun onMapLongClick(point: LatLng): Boolean {
        map.pointMapMarker(point)
        resetBottomSheetPlace()
        return true
    }
    override fun onMapClick(point: LatLng): Boolean {
        map.deleteRouteAndMarkers()
        hidePlacesBottomSheet()
        showBottomNavigation()
        commuterBinding?.directionsButton?.visibility = View.VISIBLE
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
        map.clearCache()
        commuterBinding = null
        map.getMapView().onDestroy()
        unregisterReceiver()
        super.onDestroy()


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
        changeFloatingButtonIconBlack()
    }
    override fun onMoveEnd(detector: MoveGestureDetector) {}



}