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
    fun hasInternetConnection(context: Context):Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo?.apply {
            return isConnected && isAvailable
        }
        return false
    }


    @SuppressLint("MissingPermission")
    @Suppress("Deprecation")
    fun hasLocationTurnedOn(context: Context) :Boolean {
        (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager).apply {
            return isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

    }


}