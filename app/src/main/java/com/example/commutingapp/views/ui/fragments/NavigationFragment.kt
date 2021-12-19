package com.example.commutingapp.views.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.commutingapp.BuildConfig
import com.example.commutingapp.R
import com.example.commutingapp.databinding.FragmentNavigationBinding
import com.example.commutingapp.utils.others.Constants
import com.example.commutingapp.utils.others.Constants.KEY_DESTINATION_LATITUDE
import com.example.commutingapp.utils.others.Constants.KEY_DESTINATION_LONGITUDE
import com.example.commutingapp.utils.others.Constants.KEY_LAST_LOCATION_LATITUDE
import com.example.commutingapp.utils.others.Constants.KEY_LAST_LOCATION_LONGITUDE
import com.example.commutingapp.utils.others.Constants.KEY_NAME_NAVIGATION_MAP_STYLE
import com.example.commutingapp.utils.others.Constants.KEY_NAME_SWITCH_SATELLITE_SHARED_PREFERENCE
import com.example.commutingapp.utils.others.Constants.KEY_NAME_SWITCH_TRAFFIC_SHARED_PREFERENCE
import com.example.commutingapp.utils.others.Constants.ROUTE_COLOR_HEAVY_CONGESTION
import com.example.commutingapp.utils.others.Constants.ROUTE_COLOR_LOW_CONGESTION
import com.example.commutingapp.utils.others.Constants.ROUTE_COLOR_MODERATE_CONGESTION
import com.example.commutingapp.utils.others.Constants.ROUTE_COLOR_ROAD_CLOSURE
import com.example.commutingapp.utils.others.Constants.ROUTE_COLOR_ROAD_RESTRICTED
import com.example.commutingapp.utils.others.Constants.ROUTE_COLOR_SEVERE_CONGESTION
import com.example.commutingapp.utils.others.FragmentToActivity
import com.example.commutingapp.utils.others.SwitchState
import com.example.commutingapp.views.dialogs.CustomDialogBuilder
import com.example.commutingapp.views.dialogs.DialogDirector
import com.example.commutingapp.views.ui.subComponents.Component
import com.example.commutingapp.views.ui.subComponents.TrackingBottomSheet
import com.mapbox.api.directions.v5.models.Bearing
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
import com.mapbox.maps.plugin.locationcomponent.LocationComponentConstants
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.TimeFormat
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.formatter.DistanceFormatterOptions
import com.mapbox.navigation.base.formatter.UnitType
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
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineColorResources
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources
import com.mapbox.navigation.ui.tripprogress.api.MapboxTripProgressApi
import com.mapbox.navigation.ui.tripprogress.model.*
import com.rejowan.cutetoast.CuteToast
import timber.log.Timber
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.Calendar.*

class NavigationFragment : Fragment(R.layout.fragment_navigation) {

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
    private lateinit var trackingBottomSheet: Component
    private var userDestination:Point? = null
    private var userLastLocation:Point? = null
    private lateinit var findRouteDialog: CustomDialogBuilder
    private lateinit var timeStarted: LocalTime
    private lateinit var timeFinished: LocalTime
    private var calendar:Calendar = Calendar.getInstance()
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


    private val navigationLocationProvider = NavigationLocationProvider()


    private val locationObserver = object : LocationObserver {
        var firstLocationUpdateReceived = false

        @SuppressLint("BinaryOperationInTimber")
        override fun onNewRawLocation(rawLocation: Location) {
            val location = LatLng(rawLocation.latitude,rawLocation.longitude)
             Timber.d("New Raw Location $location")
        }

        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
            val enhancedLocation = locationMatcherResult.enhancedLocation
            navigationLocationProvider.changePosition(
                location = enhancedLocation,
                keyPoints = locationMatcherResult.keyPoints,
            )

            Timber.e("Time is:  ${enhancedLocation.time}")


            viewportDataSource.onLocationChanged(enhancedLocation)
            viewportDataSource.evaluate()

            if (!firstLocationUpdateReceived) {
                firstLocationUpdateReceived = true
                navigationCamera.requestNavigationCameraToOverview(
                    stateTransitionOptions = NavigationCameraTransitionOptions.Builder()
                        .maxDuration(0) // instant transition
                        .build()
                )
            }
        }
    }

    private fun saveSatelliteSwitchStatePreference(state:String){
        satelliteSwitchButtonPreference.edit().putString(KEY_NAME_SWITCH_SATELLITE_SHARED_PREFERENCE,state).apply()
    }
    private fun saveTrafficSwitchStatePreference(state:String){
        trafficSwitchButtonPreference.edit().putString(KEY_NAME_SWITCH_TRAFFIC_SHARED_PREFERENCE,state).apply()
    }
    private fun saveMapStylePreference(style:String){
        mapStylePreference.edit().putString(KEY_NAME_NAVIGATION_MAP_STYLE,style).apply()
    }


    private fun satelliteSwitchState()=
        satelliteSwitchButtonPreference.getString(KEY_NAME_SWITCH_SATELLITE_SHARED_PREFERENCE,SwitchState.OFF.toString())

    private fun trafficSwitchState()=
        trafficSwitchButtonPreference.getString(KEY_NAME_SWITCH_TRAFFIC_SHARED_PREFERENCE,SwitchState.ON.toString())

    private fun currentMapStyle()=
        mapStylePreference.getString(KEY_NAME_NAVIGATION_MAP_STYLE,Style.TRAFFIC_NIGHT)





    private val routeProgressObserver = RouteProgressObserver { routeProgress ->
        viewportDataSource.onRouteProgressChanged(routeProgress)
        viewportDataSource.evaluate()

        val style = mapboxMap.getStyle()
        style?.let{
            val maneuverArrowResult = routeArrowApi.addUpcomingManeuverArrow(routeProgress)
            routeArrowView.renderManeuverUpdate(it, maneuverArrowResult)
        }
//todo


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

//todo
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
        satelliteSwitchButtonPreference = context.getSharedPreferences(KEY_NAME_SWITCH_SATELLITE_SHARED_PREFERENCE,Context.MODE_PRIVATE)
        trafficSwitchButtonPreference = context.getSharedPreferences(KEY_NAME_SWITCH_TRAFFIC_SHARED_PREFERENCE,Context.MODE_PRIVATE)
        mapStylePreference = context.getSharedPreferences(KEY_NAME_NAVIGATION_MAP_STYLE,Context.MODE_PRIVATE)
        try {
            this.notifyListener = context as FragmentToActivity<Fragment>
        } catch (e: ClassCastException) { }
    }
    private val routeLineResources: RouteLineResources by lazy {
        RouteLineResources.Builder()
            .routeLineColorResources(RouteLineColorResources.Builder()
                .routeClosureColor(Color.parseColor(ROUTE_COLOR_ROAD_CLOSURE))
                .restrictedRoadColor(Color.parseColor(ROUTE_COLOR_ROAD_RESTRICTED))
                .routeHeavyCongestionColor(Color.parseColor(ROUTE_COLOR_HEAVY_CONGESTION))
                .routeSevereCongestionColor(Color.parseColor(ROUTE_COLOR_SEVERE_CONGESTION))
                .routeModerateCongestionColor(Color.parseColor(ROUTE_COLOR_MODERATE_CONGESTION))
                .routeLowCongestionColor(Color.parseColor(ROUTE_COLOR_LOW_CONGESTION))
                .build())
            .build()
    }


    private val arrivalObserver = object : ArrivalObserver {

        override fun onWaypointArrival(routeProgress: RouteProgress) {
            Timber.e("ON WAY POINT ARRIVAL")
        }

        override fun onNextRouteLegStart(routeLegProgress: RouteLegProgress) {
            Timber.e("ON NEXT ROUTE LEG START")
        }


        @RequiresApi(Build.VERSION_CODES.O)
        override fun onFinalDestinationArrival(routeProgress: RouteProgress) {
            Timber.e("FINAL DESTINATION ARRIVED")
            Timber.e("Distance travelled: ${routeProgress.distanceTraveled}")
            timeFinished = getCurrentTimeStamp()
        }
      }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findRouteDialog = DialogDirector(requireActivity()).buildFindRouteDialog().apply { show() }
        mapboxMap = binding?.mapView?.getMapboxMap()!!
        mapboxMap.setBounds(cameraBoundsOptionsBuilder())
        trackingBottomSheet = Component(TrackingBottomSheet(view)).apply {show() }





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
            MapboxNavigationProvider.create(navigationOptionsBuilder())
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
            MapboxDistanceFormatter(distanceFormatterOptionsBuilder())
        )

        tripProgressApi = MapboxTripProgressApi(progressUpdateFormatter(distanceFormatterOptionsBuilder()))

        val mapboxRouteLineOptions = MapboxRouteLineOptions.Builder(requireContext())
            .displayRestrictedRoadSections(true)
            .withRouteLineBelowLayerId(LocationComponentConstants.LOCATION_INDICATOR_LAYER)
            .withRouteLineResources(routeLineResources)
            .build()

        routeLineApi = MapboxRouteLineApi(mapboxRouteLineOptions)
        routeLineView = MapboxRouteLineView(mapboxRouteLineOptions)

        val routeArrowOptions = RouteArrowOptions.Builder(requireContext()).build()
        routeArrowView = MapboxRouteArrowView(routeArrowOptions)


        setSwitchState()
        mapboxMap.loadStyleUri(currentMapStyle()?:Style.TRAFFIC_NIGHT) {
            createRoute()

        }



        providerClickListener()
        mapboxNavigation.startTripSession()

        timeStarted = getCurrentTimeStamp()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentTimeStamp() = LocalTime.of(calendar[HOUR], calendar[MINUTE], calendar[SECOND])
    private fun setSwitchState(){
        binding?.satelliteMapSwitchButton?.isChecked = satelliteSwitchState() == SwitchState.ON.toString()
        binding?.trafficMapSwitchButton?.isChecked = trafficSwitchState() == SwitchState.ON.toString()

    }

    private fun navigationOptionsBuilder()= NavigationOptions.Builder(requireActivity().applicationContext)
        .accessToken(getString(R.string.MapsToken))
        .distanceFormatterOptions(distanceFormatterOptionsBuilder())
        .build()// todo add incident options
    private fun cameraBoundsOptionsBuilder()=CameraBoundsOptions.Builder()
        .minZoom(Constants.MIN_ZOOM_LEVEL_MAPS)
        .build()
    private fun distanceFormatterOptionsBuilder()=DistanceFormatterOptions.Builder(requireContext().applicationContext)
        .unitType(UnitType.METRIC)
        .build()

    private fun progressUpdateFormatter(distanceFormatterOptions: DistanceFormatterOptions)=TripProgressUpdateFormatter.Builder(requireContext())
        .distanceRemainingFormatter(DistanceRemainingFormatter(distanceFormatterOptions))
        .timeRemainingFormatter(TimeRemainingFormatter(requireContext()))
        .percentRouteTraveledFormatter(PercentDistanceTraveledFormatter())
        .estimatedTimeToArrivalFormatter(EstimatedTimeToArrivalFormatter(requireContext(), TimeFormat.NONE_SPECIFIED))
        .build()

    private fun createRoute(){
        this.arguments?.let {
            val destination = Point.fromLngLat(it.getDouble(KEY_DESTINATION_LONGITUDE),it.getDouble(KEY_DESTINATION_LATITUDE))
            val lastLocation = Point.fromLngLat(it.getDouble(KEY_LAST_LOCATION_LONGITUDE),it.getDouble(KEY_LAST_LOCATION_LATITUDE))
            findRoute(destination,lastLocation)
            userDestination = destination
            userLastLocation = lastLocation
        }
    }
    private fun providerClickListener(){

        binding?.stop?.setOnClickListener {
            clearRouteAndStopNavigation()
            requireActivity().onBackPressed()
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
            trackingBottomSheet.hide()
        }

        binding?.satelliteMapSwitchButton?.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                changeSatelliteModeOn()
                saveSatelliteSwitchStatePreference(SwitchState.ON.toString())
            }else{
                changeSatelliteModeOff()
                saveSatelliteSwitchStatePreference(SwitchState.OFF.toString())
            }
            trackingBottomSheet.hide()
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()

        mapboxNavigation.registerArrivalObserver(arrivalObserver)
        mapboxNavigation.registerRoutesObserver(routesObserver)
        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
        mapboxNavigation.registerLocationObserver(locationObserver)


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStop() {
        super.onStop()
        mapboxNavigation.unregisterArrivalObserver(arrivalObserver)
        mapboxNavigation.unregisterRoutesObserver(routesObserver)
        mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
        mapboxNavigation.unregisterLocationObserver(locationObserver)
        mapboxNavigation.stopTripSession()


        val hours: Long = ChronoUnit.HOURS.between(timeStarted, timeFinished)
        val minutes: Long = ChronoUnit.MINUTES.between(timeStarted, timeStarted) % 6
        val seconds: Long = ChronoUnit.SECONDS.between(timeStarted, timeStarted) % 60

        Timber.e("Different is ")
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        MapboxNavigationProvider.destroy()
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
                        requireActivity().onBackPressed()
                    }

                    override fun onCanceled(routeOptions: RouteOptions, routerOrigin: RouterOrigin) {
                        Timber.e("ON CANCELED")
                        findRouteDialog.cancel()
                    }
                }
            )
    }
    private fun routeOptionsBuilder(lastLocation: Point,destinationLocation: Point)= RouteOptions.builder()
        .applyDefaultNavigationOptions()
        .applyLanguageAndVoiceUnitOptions(requireContext())
        .coordinatesList(listOf(lastLocation, destinationLocation))
        .bearingsList(
            listOf(
                Bearing.builder()
                    .angle(360.0)
                    .degrees(45.0)
                    .build(),
                null
            )
        )
        .build()
    private fun setRouteAndStartNavigation(routes: List<DirectionsRoute>) {
        mapboxNavigation.setRoutes(routes)
        navigationCamera.requestNavigationCameraToOverview()
    }

    private fun clearRouteAndStopNavigation() {
        mapboxNavigation.setRoutes(listOf())
    }



}