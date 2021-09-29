package com.example.commutingapp.utils.InternetConnection

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.example.commutingapp.utils.InternetConnection.Connection.hasInternetConnection

object Connection {





    @SuppressLint("MissingPermission")
    @Suppress("Deprecation")
    fun Context.hasInternetConnection() =
        (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo?.apply {
             isConnected && isAvailable
        }



    @SuppressLint("MissingPermission")
    @Suppress("Deprecation")
    fun Context.hasLocationTurnedOn()=
        (getSystemService(Context.LOCATION_SERVICE) as LocationManager).apply {
             isProviderEnabled(LocationManager.GPS_PROVIDER) && isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }



}