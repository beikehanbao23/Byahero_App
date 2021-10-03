package com.example.commutingapp.views.ui.fragments


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.commutingapp.R
import com.example.commutingapp.data.others.Constants.ACTION_PAUSE_SERVICE
import com.example.commutingapp.data.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.commutingapp.data.others.Constants.ACTION_STOP_SERVICE
import com.example.commutingapp.data.others.Constants.DEFAULT_MAP_ZOOM
import com.example.commutingapp.data.others.Constants.POLYLINE_COLOR
import com.example.commutingapp.data.others.Constants.POLYLINE_WIDTH
import com.example.commutingapp.data.others.TrackingPermissionUtility.hasLocationPermission
import com.example.commutingapp.data.others.TrackingPermissionUtility.requestPermission
import com.example.commutingapp.data.service.TrackingService
import com.example.commutingapp.data.service.innerPolyline
import com.example.commutingapp.utils.InternetConnection.Connection
import com.example.commutingapp.viewmodels.MainViewModel
import com.example.commutingapp.views.dialogs.DialogDirector
import com.mapbox.mapboxsdk.annotations.PolylineOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.traffic.TrafficPlugin
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class CommuterFragment : Fragment(R.layout.commuter_fragment), EasyPermissions.PermissionCallbacks,
    OnMapReadyCallback {

    private val viewModel: MainViewModel by viewModels()

    private var map: MapboxMap? = null
    private var isTracking = false
    private var outerPolyline = mutableListOf<innerPolyline>()
    private lateinit var mapView: MapView
    private lateinit var buttonStart: Button
    private lateinit var buttonStop: Button


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonStart = view.findViewById(R.id.startButton)
        buttonStop = view.findViewById(R.id.finishButton)

        buttonStart.setOnClickListener {
            requestRequiredSettings()
            toogleStartButton()
        }
        buttonStop.setOnClickListener { sendCommandToTrackingService(ACTION_STOP_SERVICE) }



        mapView = view.findViewById(R.id.googleMapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        mapView.setOnLongClickListener {
            Toast.makeText(requireContext(), "Long pressed", Toast.LENGTH_SHORT).show()
            true
        }
        subscribeToObservers()


    }



    override fun onMapReady(mapboxMap: MapboxMap) {
        map = mapboxMap.apply {
            uiSettings.isAttributionEnabled = false

            uiSettings.isLogoEnabled = false
            setStyle(
                Style.MAPBOX_STREETS
            ) { style ->
                TrafficPlugin(mapView, mapboxMap, style).apply {
                    setVisibility(true)
                }
            }
        }
        addAllPolylines()

    }


    private fun subscribeToObservers() {
        TrackingService().isTracking().observe(viewLifecycleOwner) {
            isTracking = it
            updateButtons()
        }

        TrackingService().outerPolyline().observe(viewLifecycleOwner) {
            outerPolyline = it
            addLatestPolyline()
            moveCameraToUser()
        }
    }

    private fun sendCommandToTrackingService(action: String) {
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    private fun toogleStartButton() {
        if (isTracking) {
            sendCommandToTrackingService(ACTION_PAUSE_SERVICE)
            return
        }
        sendCommandToTrackingService(ACTION_START_OR_RESUME_SERVICE)
    }

    private fun updateButtons() {

        if (isTracking) {
            buttonStart.text = "Stop"
            buttonStop.visibility = View.GONE
            return
        }
        buttonStart.text = "Start"
        buttonStop.visibility = View.VISIBLE
    }


    private fun moveCameraToUser() {
        if (hasExistingInnerAndOuterPolyLines()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    outerPolyline.last().last(),
                    DEFAULT_MAP_ZOOM
                )
            )
        }
    }

    private fun addAllPolylines() {

        outerPolyline.forEach {
            customPolylineAppearance().addAll(it).apply {
                map?.addPolyline(this)
            }
        }
    }

    private fun addLatestPolyline() {
        if (hasExistingInnerPolyLines()) {
            val innerPolylinePosition = outerPolyline.last().size - 2
            val preLastLatLng = outerPolyline.last()[innerPolylinePosition]
            val lastLatLng = outerPolyline.last().last()

            customPolylineAppearance()
                .add(preLastLatLng)
                .add(lastLatLng).apply {
                    map?.addPolyline(this)

                }

        }
    }


    private fun customPolylineAppearance(): PolylineOptions {

        return PolylineOptions()
            .color(POLYLINE_COLOR)
            .width(POLYLINE_WIDTH)


    }


    private fun hasExistingInnerAndOuterPolyLines() =
        outerPolyline.last().isNotEmpty() && outerPolyline.isNotEmpty()

    private fun hasExistingInnerPolyLines() =
        outerPolyline.isNotEmpty() && outerPolyline.last().size > 1


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    private fun requestRequiredSettings() {
        if (hasLocationPermission(requireContext())) {
            if (!Connection.hasLocationTurnedOn(requireContext())) {
                DialogDirector(requireActivity()).constructRequestLocationDialog()
            }
            return
        }
        requestPermission(this)

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

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}


}