package com.example.commutingapp.views.ui.fragments


import android.annotation.SuppressLint
import android.content.Intent

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast

import androidx.collection.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.commutingapp.R
import com.example.commutingapp.data.others.BitmapConvert.getBitmapFromVectorDrawable
import com.example.commutingapp.data.others.Constants
import com.example.commutingapp.data.others.Constants.ACTION_PAUSE_SERVICE
import com.example.commutingapp.data.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.commutingapp.data.others.Constants.ACTION_STOP_SERVICE
import com.example.commutingapp.data.others.Constants.DEFAULT_LATITUDE
import com.example.commutingapp.data.others.Constants.DEFAULT_LONGTITUDE
import com.example.commutingapp.data.others.Constants.DEFAULT_MAP_ZOOM
import com.example.commutingapp.data.others.Constants.TRACKING_MAP_ZOOM
import com.example.commutingapp.data.others.Constants.LAST_KNOWN_LOCATION_MAP_ZOOM
import com.example.commutingapp.data.others.Constants.MAP_MARKER_IMAGE_NAME
import com.example.commutingapp.data.others.Constants.MAP_MARKER_SIZE
import com.example.commutingapp.data.others.Constants.POLYLINE_COLOR
import com.example.commutingapp.data.others.Constants.POLYLINE_WIDTH
import com.example.commutingapp.data.others.Constants.REQUEST_CHECK_SETTING
import com.example.commutingapp.data.others.TrackingPermissionUtility.hasLocationPermission
import com.example.commutingapp.data.others.TrackingPermissionUtility.requestPermission
import com.example.commutingapp.data.service.TrackingService
import com.example.commutingapp.data.service.innerPolyline
import com.example.commutingapp.viewmodels.MainViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.mapbox.mapboxsdk.annotations.PolylineOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.plugins.traffic.TrafficPlugin
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import com.mapbox.mapboxsdk.camera.CameraPosition





@AndroidEntryPoint
class CommuterFragment : Fragment(R.layout.commuter_fragment), EasyPermissions.PermissionCallbacks,
    OnMapReadyCallback,MapboxMap.OnMapLongClickListener,OnSymbolClickListener {

    private val mainViewModel: MainViewModel by viewModels()

    private var mapBoxMap: MapboxMap? = null
    private var isTracking = false
    private var outerPolyline = mutableListOf<innerPolyline>()
    private var mapBoxView: MapView? = null
    private lateinit var buttonStart: Button
    private lateinit var buttonStop: Button
    private lateinit var mapBoxStyle: Style
    private lateinit var mapMarkerSymbol: SymbolManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonStart = view.findViewById(R.id.startButton)
        buttonStop = view.findViewById(R.id.finishButton)
        buttonStart.setOnClickListener {
            if (requestPermissionGranted()) {
                checkLocationSetting()
            }
        }
        buttonStop.setOnClickListener() { sendCommandToTrackingService(ACTION_STOP_SERVICE) }

        mapBoxView = view.findViewById(R.id.googleMapView)

        mapBoxView?.apply {
            onCreate(savedInstanceState)
            isClickable = true
        }?.also {
            it.getMapAsync(this)
        }
        subscribeToObservers()


    }

    @SuppressLint("MissingPermission")
    private fun moveCameraToLastKnownLocation(){
        LocationServices.getFusedLocationProviderClient(requireActivity()).apply {
            lastLocation.addOnSuccessListener {
            moveCameraToUser(LatLng(it.latitude,it.longitude), LAST_KNOWN_LOCATION_MAP_ZOOM)
        }
            lastLocation.addOnFailureListener {
                moveCameraToUser(LatLng(DEFAULT_LATITUDE, DEFAULT_LONGTITUDE), DEFAULT_MAP_ZOOM)
            }
        }


    }

    companion object {
        val request: LocationRequest = LocationRequest.create().apply {
            interval = Constants.NORMAL_LOCATION_UPDATE_INTERVAL
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
                getLocationResult(this)
            }


    }

    private fun getLocationResult(result: Task<LocationSettingsResponse>) {
        result.addOnCompleteListener {
            try {
                it.getResult(ApiException::class.java)
                toggleStartButton()
            } catch (e: ApiException) {
                handleLocationResultException(e)
            }
        }
    }

    private fun handleLocationResultException(e:ApiException){
        when (e.statusCode) {
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                val resolvableApiException = e as ResolvableApiException
                resolvableApiException.startResolutionForResult(
                    requireActivity(),
                    REQUEST_CHECK_SETTING
                )
            }
        }
    }
    override fun onMapReady(mapboxMap: MapboxMap) {
        mapBoxMap = mapboxMap.apply {
            uiSettings.isAttributionEnabled = false
            uiSettings.isLogoEnabled = false
        }
        mapBoxMap?.addOnMapLongClickListener(this)
        addMapStyle(mapboxMap)
        moveCameraToLastKnownLocation()
    }



    private fun addMapStyle(mapboxMap: MapboxMap) {
        mapboxMap.setStyle(Style.DARK) { style->
            mapBoxView?.let {mapView->
                TrafficPlugin(mapView, mapboxMap, style).apply { setVisibility(true) }
                enableLocationComponent(style)
                mapBoxStyle = style
                mapMarkerSymbol = SymbolManager(mapView, mapboxMap, mapBoxStyle).also {symbolManager->
                    symbolManager.addClickListener(this)
                }
            }
        }

    }

    override fun onAnnotationClick(symbol: Symbol?): Boolean {
        mapMarkerSymbol.delete(symbol)
        return true
    }

    override fun onMapLongClick(point: LatLng): Boolean {
      pointMapMarker(point)
      return true
    }



    private fun pointMapMarker(latLng: LatLng) {
        mapBoxStyle.addImage(MAP_MARKER_IMAGE_NAME,getBitmapFromVectorDrawable(requireContext(), R.drawable.ic_location))
        if(!hasExistingMapMarker()) {
            createMapMarker(latLng)
            mapBoxMap.let {
                it?.cameraPosition?.zoom.apply {
                    this?.let { zoomLevel -> moveCameraToUser(latLng, zoomLevel) }
                }
            }
        }
    }
    private fun hasExistingMapMarker() = mapMarkerSymbol.annotations.size != 0
    private fun createMapMarker(latLng: LatLng){

            mapMarkerSymbol.create(
                SymbolOptions()
                    .withLatLng(latLng)
                    .withIconImage(MAP_MARKER_IMAGE_NAME)
                    .withIconSize(MAP_MARKER_SIZE))

    }


    private fun enableLocationComponent(style:Style){
        LocationComponentOptions.builder(requireContext())
            .build().also { componentOptions->
                mapBoxMap?.locationComponent?.apply {
                    activateLocationComponent(
                        LocationComponentActivationOptions.builder(requireContext(), style)
                            .locationComponentOptions(componentOptions)
                            .build())
                    createComponentsLocation(this)
                }
            }
    }



    @SuppressLint("MissingPermission")
    private fun createComponentsLocation(locationComponent:LocationComponent) {
        locationComponent.apply {
            isLocationComponentEnabled = true;
            renderMode = RenderMode.NORMAL;
            zoomWhileTracking(TRACKING_MAP_ZOOM)
        }
    }

    private fun subscribeToObservers() {
        TrackingService().isCurrentlyTracking().observe(viewLifecycleOwner) {
            isTracking = it
            updateButtons()
        }

        TrackingService().outerPolyline().observe(viewLifecycleOwner) {
            outerPolyline = it

            addLatestPolyline()
            if (hasExistingInnerAndOuterPolyLines()) {
                moveCameraToUser(outerPolyline.last().last(), TRACKING_MAP_ZOOM)
            }
        }
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
            buttonStart.text =  getString(R.string.stopButton)
            buttonStop.visibility = View.GONE
            return
        }

        buttonStart.text =  getString(R.string.startButton)
        buttonStop.visibility = View.VISIBLE
    }


    private fun moveCameraToUser(latLng: LatLng,zoomLevel:Double) {
        mapBoxMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng,
                zoomLevel
            )
        )

    }


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
        outerPolyline.isNotEmpty()  &&  outerPolyline.last().isNotEmpty()

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



}