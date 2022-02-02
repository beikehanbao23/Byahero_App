package com.example.commutingapp.views.ui.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.location.Location
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.commutingapp.BuildConfig
import com.example.commutingapp.R
import com.example.commutingapp.databinding.FragmentNavigationBinding
import com.example.commutingapp.utils.others.Constants.KEY_NAVIGATION_MAP_STYLE
import com.example.commutingapp.utils.others.Constants.KEY_SWITCH_SATELLITE
import com.example.commutingapp.utils.others.Constants.KEY_SWITCH_TRAFFIC
import com.example.commutingapp.utils.others.FragmentToActivity
import com.example.commutingapp.utils.others.SwitchState
import com.example.commutingapp.views.dialogs.CustomDialogBuilder
import com.example.commutingapp.views.dialogs.DialogDirector
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.formatter.DistanceFormatterOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.RouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.base.trip.model.RouteLegProgress
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.arrival.ArrivalObserver
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.formatter.MapboxDistanceFormatter
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.ui.maneuver.api.MapboxManeuverApi
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.camera.lifecycle.NavigationBasicGesturesHandler
import com.mapbox.navigation.ui.maps.camera.state.NavigationCameraState
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraTransitionOptions
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.arrow.model.RouteArrowOptions
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.maps.route.line.model.RouteLine
import com.mapbox.navigation.ui.tripprogress.api.MapboxTripProgressApi
import com.mapbox.navigation.ui.tripprogress.model.TripProgressUpdateFormatter
import com.rejowan.cutetoast.CuteToast
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class NavigationFragment : Fragment(R.layout.fragment_navigation) {


    @Inject lateinit var distanceFormatterOptionsBuilder: DistanceFormatterOptions
    @Inject lateinit var routeOptionsBuilder:RouteOptions.Builder
    @Inject lateinit var progressUpdateFormatterOptions: TripProgressUpdateFormatter
    @Inject lateinit var cameraBoundsOptionsBuilder:CameraBoundsOptions
    @Inject lateinit var navigationOptionsBuilder:NavigationOptions
    @Inject lateinit var routeLineOptionsBuilder: MapboxRouteLineOptions
    @Inject lateinit var navigationCameraTransitionOptions: NavigationCameraTransitionOptions

    private val navigationArgs:NavigationFragmentArgs by navArgs()
    private companion object {
        private const val BUTTON_ANIMATION_DURATION = 1500L
    }

    private lateinit var satelliteSwitchButtonPreference:SharedPreferences
    private lateinit var trafficSwitchButtonPreference:SharedPreferences
    private lateinit var mapStylePreference:SharedPreferences
    private var binding: FragmentNavigationBinding? = null
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapboxNavigation: MapboxNavigation
    private lateinit var navigationCamera: NavigationCamera
    private lateinit var viewportDataSource: MapboxNavigationViewportDataSource
    private lateinit var notifyListener: FragmentToActivity<Fragment>
    private val pixelDensity = Resources.getSystem().displayMetrics.density

    private var userDestination:Point? = null
    private var userLastLocation:Point? = null
    private lateinit var findRouteDialog: CustomDialogBuilder

    private var mediaPlayer:MediaPlayer = MediaPlayer()

    private lateinit var persistentBottomSheet:BottomSheetBehavior<View>
    private val overviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            140.0 * pixelDensity,
            40.0 * pixelDensity,
            120.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }
    private val followingPadding: EdgeInsets by lazy {
        EdgeInsets(
            180.0 * pixelDensity,
            40.0 * pixelDensity,
            150.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }

    private lateinit var maneuverApi: MapboxManeuverApi
    private lateinit var tripProgressApi: MapboxTripProgressApi
    private lateinit var routeLineApi: MapboxRouteLineApi
    private lateinit var routeLineView: MapboxRouteLineView
    private val routeArrowApi: MapboxRouteArrowApi = MapboxRouteArrowApi()
    private lateinit var routeArrowView: MapboxRouteArrowView
    private lateinit var notificationManager: NotificationManagerCompat

    private val navigationLocationProvider = NavigationLocationProvider()
    private val locationObserver = object : LocationObserver {
        var firstLocationUpdateReceived = false

        @SuppressLint("BinaryOperationInTimber")
        @Suppress("Warnings")
        override fun onNewRawLocation(rawLocation: Location) {

            val userLocation = LatLng(rawLocation.latitude, rawLocation.longitude)

            userDestination?.let { destination ->

                val distance = Math.round( userLocation.distanceTo(LatLng(destination.latitude(), destination.longitude())) /10 ) * 10
                makeAlerts(distance)
                Timber.e("New Raw Location")
            }

        }

        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {


            Timber.e("New Location Matcher")

            val enhancedLocation = locationMatcherResult.enhancedLocation
            navigationLocationProvider.changePosition(
                location = enhancedLocation,
                keyPoints = locationMatcherResult.keyPoints,
            )


            viewportDataSource.onLocationChanged(enhancedLocation)
            viewportDataSource.evaluate()

            if (!firstLocationUpdateReceived) {
                firstLocationUpdateReceived = true
                navigationCamera.requestNavigationCameraToOverview(
                    stateTransitionOptions = navigationCameraTransitionOptions
                )
            }
        }
    }




//todo fix this later
    private fun makeAlerts(distance:Long){

        if(distance in 990..1000){
            vibratePhone(500L)
            playVoiceAlerts(R.raw.destination_in_1000m)
            return
        }

        if(distance in 490..500){
            vibratePhone(500L)
            playVoiceAlerts(R.raw.destination_in_500m)
            return
        }

        if(distance in 290..300){
            vibratePhone(500L)
            playVoiceAlerts(R.raw.destination_in_300m)
            return
        }

        if(distance in 140..150){
            vibratePhone(500L)
            playVoiceAlerts(R.raw.destination_in_150m)
            return
        }

        if(distance in 40..50){
            vibratePhone(1000L)
            playVoiceAlerts(R.raw.destination_in_50m)
            return
        }


    }





    private fun vibratePhone(millis:Long) {
        val vibrator =  (requireContext().getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(millis)
        }
    }

    private fun saveSatelliteSwitchStatePreference(state:String){
        satelliteSwitchButtonPreference.edit().putString(KEY_SWITCH_SATELLITE,state).apply()
    }
    private fun saveTrafficSwitchStatePreference(state:String){
        trafficSwitchButtonPreference.edit().putString(KEY_SWITCH_TRAFFIC,state).apply()
    }
    private fun saveMapStylePreference(style:String){
        mapStylePreference.edit().putString(KEY_NAVIGATION_MAP_STYLE,style).apply()
    }


    private fun satelliteSwitchState()=
        satelliteSwitchButtonPreference.getString(KEY_SWITCH_SATELLITE,SwitchState.OFF.toString())

    private fun trafficSwitchState()=
        trafficSwitchButtonPreference.getString(KEY_SWITCH_TRAFFIC,SwitchState.ON.toString())

    private fun currentMapStyle()=
        mapStylePreference.getString(KEY_NAVIGATION_MAP_STYLE,Style.TRAFFIC_NIGHT)





    private val routeProgressObserver = RouteProgressObserver { routeProgress ->
        viewportDataSource.onRouteProgressChanged(routeProgress)
        viewportDataSource.evaluate()

        val style = mapboxMap.getStyle()
        style?.let{
            val maneuverArrowResult = routeArrowApi.addUpcomingManeuverArrow(routeProgress)
            routeArrowView.renderManeuverUpdate(it, maneuverArrowResult)
        }


        val maneuvers = maneuverApi.getManeuvers(routeProgress)
        maneuvers.fold(
            { error ->
                Toast.makeText(requireContext(), error.errorMessage, Toast.LENGTH_SHORT).show()
            },
            {
                binding?.maneuverView?.visibility = View.VISIBLE
                binding?.maneuverView?.renderManeuvers(maneuvers)!!

            }
        )

        binding?.tripProgressView?.render(
            tripProgressApi.getTripProgress(routeProgress)
        )
    }
    private val routesObserver = RoutesObserver { routeUpdateResult ->
        if (routeUpdateResult.routes.isNotEmpty()) {

            val routeLines = routeUpdateResult.routes.map { RouteLine(it, null) }

            routeLineApi.setRoutes(
                routeLines
            ) { value ->
                mapboxMap.getStyle()?.apply {
                    routeLineView.renderRouteDrawData(this, value)
                }
            }

            viewportDataSource.onRouteChanged(routeUpdateResult.routes.first())
            viewportDataSource.evaluate()
        } else {

            val style = mapboxMap.getStyle()
            style?.let{
                routeLineApi.clearRouteLine { value ->
                    routeLineView.renderClearRouteLineValue(
                        it,
                        value
                    )
                }
                routeArrowView.render(it, routeArrowApi.clearArrows())
            }


            viewportDataSource.clearRouteData()
            viewportDataSource.evaluate()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNavigationBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @Suppress("Warnings")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        satelliteSwitchButtonPreference = context.getSharedPreferences(KEY_SWITCH_SATELLITE,Context.MODE_PRIVATE)
        trafficSwitchButtonPreference = context.getSharedPreferences(KEY_SWITCH_TRAFFIC,Context.MODE_PRIVATE)
        mapStylePreference = context.getSharedPreferences(KEY_NAVIGATION_MAP_STYLE,Context.MODE_PRIVATE)
        try {
            this.notifyListener = context as FragmentToActivity<Fragment>
        } catch (e: ClassCastException) { }
    }


    private val arrivalObserver = object : ArrivalObserver {

        override fun onWaypointArrival(routeProgress: RouteProgress) {
            Timber.e("ON WAY POINT ARRIVAL")


        }
        override fun onNextRouteLegStart(routeLegProgress: RouteLegProgress) {
            Timber.e("ON NEXT ROUTE LEG START")


        }
        override fun onFinalDestinationArrival(routeProgress: RouteProgress) {
            navigationCompleted()
        }
      }


    private fun hidePersistentBottomSheet(){
        persistentBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findRouteDialog = DialogDirector(requireActivity()).buildFindRouteDialog().apply { show() }
        mapboxMap = binding?.mapView?.getMapboxMap()!!
        notificationManager = NotificationManagerCompat.from(requireContext())
        mapboxMap.setBounds(cameraBoundsOptionsBuilder)
        persistentBottomSheet = BottomSheetBehavior.from(view.findViewById(R.id.bottomSheetTrackingState)).apply {
            isHideable=false
        }


        binding?.mapView?.location?.apply {
            this.locationPuck = LocationPuck2D(
                bearingImage = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.mapbox_navigation_puck_icon
                )
            )
            setLocationProvider(navigationLocationProvider)
            enabled = true
        }


        mapboxNavigation = if (MapboxNavigationProvider.isCreated()) {
            MapboxNavigationProvider.retrieve()
        } else {
            MapboxNavigationProvider.create(navigationOptionsBuilder)
        }



        viewportDataSource = MapboxNavigationViewportDataSource(mapboxMap)
        navigationCamera = NavigationCamera(
            mapboxMap,
            binding!!.mapView.camera,
            viewportDataSource
        )

        binding!!.mapView.camera.addCameraAnimationsLifecycleListener(
            NavigationBasicGesturesHandler(navigationCamera)
        )

        navigationCamera.registerNavigationCameraStateChangeObserver { navigationCameraState ->

            when (navigationCameraState) {
                NavigationCameraState.TRANSITION_TO_FOLLOWING,
                NavigationCameraState.FOLLOWING -> binding?.recenter?.visibility = View.INVISIBLE
                NavigationCameraState.TRANSITION_TO_OVERVIEW,
                NavigationCameraState.OVERVIEW,
                NavigationCameraState.IDLE -> binding?.recenter?.visibility = View.VISIBLE
            }
        }

        viewportDataSource.overviewPadding = overviewPadding
        viewportDataSource.followingPadding = followingPadding


        maneuverApi = MapboxManeuverApi(
            MapboxDistanceFormatter(distanceFormatterOptionsBuilder)
        )

        tripProgressApi = MapboxTripProgressApi(progressUpdateFormatterOptions)



        routeLineApi = MapboxRouteLineApi(routeLineOptionsBuilder)
        routeLineView = MapboxRouteLineView(routeLineOptionsBuilder)

        val routeArrowOptions = RouteArrowOptions.Builder(requireContext()).build()
        routeArrowView = MapboxRouteArrowView(routeArrowOptions)


        setSwitchState()
        mapboxMap.loadStyleUri(currentMapStyle()?:Style.TRAFFIC_NIGHT) {
            createRoute()
        }


        providerClickListener()
        mapboxNavigation.startTripSession()

    }

    private fun playVoiceAlerts(resId:Int){

        if(!mediaPlayer.isPlaying){
            mediaPlayer = MediaPlayer.create(requireContext(), resId)
            mediaPlayer.setScreenOnWhilePlaying(true)
            mediaPlayer.setVolume(1.0f,1.0f)
            mediaPlayer.start()
        }
    }
    private fun setSwitchState(){
        binding?.satelliteMapSwitchButton?.isChecked = satelliteSwitchState() == SwitchState.ON.toString()
        binding?.trafficMapSwitchButton?.isChecked = trafficSwitchState() == SwitchState.ON.toString()

    }




    private fun createRoute(){
    navigationArgs.destinationLocation?.let { target ->
        navigationArgs.lastKnownLocation?.let { origin->
            val destination = Point.fromLngLat(target.longitude,target.latitude)
            val lastLocation = Point.fromLngLat(origin.longitude,origin.latitude)
            findRoute(destination,lastLocation)
            userDestination = destination
            userLastLocation = lastLocation
        }
    }

    }


    private fun navigationCompleted(){
        binding!!.maneuverView.visibility = View.GONE
        persistentBottomSheet.isHideable = true
        persistentBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        vibratePhone(500)
        playVoiceAlerts(R.raw.destination_reached)
        mapboxNavigation.unregisterRoutesObserver(routesObserver)
        mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
        mapboxNavigation.unregisterLocationObserver(locationObserver)
        mapboxNavigation.stopTripSession()
        clearRouteAndStopNavigation()
        MapboxNavigationProvider.destroy()


        DialogDirector(requireActivity()).buildNavigationCompletedDialog().apply {
            findViewById<View>(R.id.btn_done)?.setOnClickListener {
                Navigation.findNavController(binding!!.root).navigate(R.id.navigation_fragment_to_commuter_fragment)
                dismiss()
            }
        }

    }
    private fun providerClickListener(){

        binding?.stop?.setOnClickListener {



            AlertDialog.Builder(requireContext())
            .setTitle("Cancel the Commute?")
            .setMessage("Are you sure to cancel the current Commute and delete all its data?")
            .setPositiveButton("YES") { _, _ ->

                Navigation.findNavController(binding!!.root).navigate(R.id.navigation_fragment_to_commuter_fragment)
                
            }.setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }.also { dialog->
                dialog.show()
        }







        }


        binding?.recenter?.setOnClickListener {
            navigationCamera.requestNavigationCameraToFollowing()
        }

        binding?.routeOverview?.setOnClickListener {
            navigationCamera.requestNavigationCameraToOverview()
            binding?.recenter?.showTextAndExtend(BUTTON_ANIMATION_DURATION)
        }

        binding?.trafficMapSwitchButton?.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                changeTrafficModeOn()
                saveTrafficSwitchStatePreference(SwitchState.ON.toString())
            }else{
                changeTrafficModeOff()
                saveTrafficSwitchStatePreference(SwitchState.OFF.toString())
            }
            hidePersistentBottomSheet()
        }

        binding?.satelliteMapSwitchButton?.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                changeSatelliteModeOn()
                saveSatelliteSwitchStatePreference(SwitchState.ON.toString())
            }else{
                changeSatelliteModeOff()
                saveSatelliteSwitchStatePreference(SwitchState.OFF.toString())
            }
            hidePersistentBottomSheet()
        }

    }
    private fun changeSatelliteModeOn(){
        mapboxMap.getStyle()?.styleURI.run {
            if(this == Style.TRAFFIC_NIGHT){
                changeMapStyleAndContinueExistingRoute(Style.TRAFFIC_DAY)
                saveMapStylePreference(Style.TRAFFIC_DAY)
                return@run
            }
            if(this == BuildConfig.MAP_STYLE_NIGHT){
                changeMapStyleAndContinueExistingRoute(BuildConfig.MAP_STYLE_DAY)
                saveMapStylePreference(BuildConfig.MAP_STYLE_DAY)
            }
        }

    }
    private fun changeSatelliteModeOff(){
        mapboxMap.getStyle()?.styleURI.run {
         if(this == Style.TRAFFIC_DAY){
             changeMapStyleAndContinueExistingRoute(Style.TRAFFIC_NIGHT)
             saveMapStylePreference(Style.TRAFFIC_NIGHT)
             return@run
         }
         if(this == BuildConfig.MAP_STYLE_DAY){
             changeMapStyleAndContinueExistingRoute(BuildConfig.MAP_STYLE_NIGHT)
             saveMapStylePreference(BuildConfig.MAP_STYLE_NIGHT)
         }
        }
    }
    private fun changeTrafficModeOn(){
    mapboxMap.getStyle()?.styleURI.run {
        if(this == BuildConfig.MAP_STYLE_DAY){
            changeMapStyleAndContinueExistingRoute(Style.TRAFFIC_DAY)
            saveMapStylePreference(Style.TRAFFIC_DAY)
            return@run
        }
        if(this == BuildConfig.MAP_STYLE_NIGHT){
            changeMapStyleAndContinueExistingRoute(Style.TRAFFIC_NIGHT)
            saveMapStylePreference(Style.TRAFFIC_NIGHT)
        }
    }
    }
    private fun changeTrafficModeOff(){
        mapboxMap.getStyle()?.styleURI.run {
            if(this == Style.TRAFFIC_DAY){
                changeMapStyleAndContinueExistingRoute(BuildConfig.MAP_STYLE_DAY)
                saveMapStylePreference(BuildConfig.MAP_STYLE_DAY)
                return@run
            }

            if(this == Style.TRAFFIC_NIGHT){
                changeMapStyleAndContinueExistingRoute(BuildConfig.MAP_STYLE_NIGHT)
                saveMapStylePreference(BuildConfig.MAP_STYLE_NIGHT)
            }
        }
    }
    private fun changeMapStyleAndContinueExistingRoute(style:String){

        userDestination?.let { destination ->
            userLastLocation?.let {lastLocation->
                findRoute(destination, lastLocation)
                mapboxMap.loadStyleUri(style)
            }
        }
    }



    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        mapboxNavigation.registerArrivalObserver(arrivalObserver)
        mapboxNavigation.registerRoutesObserver(routesObserver)
        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
        mapboxNavigation.registerLocationObserver(locationObserver)


    }
    override fun onDestroy() {
        try {

            binding = null
            mediaPlayer.release()
            mapboxNavigation.unregisterArrivalObserver(arrivalObserver)
            mapboxNavigation.unregisterRoutesObserver(routesObserver)
            mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
            mapboxNavigation.unregisterLocationObserver(locationObserver)
            mapboxNavigation.stopTripSession()
            clearRouteAndStopNavigation()
            MapboxNavigationProvider.destroy()
            super.onDestroy()
        }catch (e:IllegalStateException){
            Timber.e("Navigation onDestroy : ${e.message}")
        }
    }


    private fun findRoute(destinationLocation: Point, lastLocation:Point) {
            mapboxNavigation.requestRoutes(routeOptionsBuilder(lastLocation, destinationLocation), object : RouterCallback {

                    override fun onRoutesReady(routes: List<DirectionsRoute>, routerOrigin: RouterOrigin) {
                        setRouteAndStartNavigation(routes)
                        findRouteDialog.cancel()
                    }
                    override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
                        CuteToast.ct(requireContext(), requireActivity().getString(R.string.unreachableDestination), CuteToast.LENGTH_LONG, CuteToast.WARN, true).show()
                        findRouteDialog.cancel()
                        Navigation.findNavController(binding!!.root).navigate(R.id.navigation_fragment_to_commuter_fragment)

                    }

                    override fun onCanceled(routeOptions: RouteOptions, routerOrigin: RouterOrigin) {
                        Timber.e("ON CANCELED")
                        findRouteDialog.cancel()
                    }
                }
            )
    }
    private fun routeOptionsBuilder(lastLocation: Point,destinationLocation: Point) = routeOptionsBuilder
        .coordinatesList(listOf(lastLocation, destinationLocation))
        .build()


    private fun setRouteAndStartNavigation(routes: List<DirectionsRoute>) {
        mapboxNavigation.setRoutes(routes)
        navigationCamera.requestNavigationCameraToOverview()
    }
    private fun clearRouteAndStopNavigation() {
        mapboxNavigation.setRoutes(listOf())
    }



}