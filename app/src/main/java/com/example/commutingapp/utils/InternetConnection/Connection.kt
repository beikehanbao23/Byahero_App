package com.example.commutingapp.utils.InternetConnection

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.location.LocationManager
import android.location.LocationManager.MODE_CHANGED_ACTION
import android.location.LocationManager.PROVIDERS_CHANGED_ACTION
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.example.commutingapp.utils.InternetConnection.Connection.hasInternetConnection
import android.text.TextUtils

import android.provider.Settings.SettingNotFoundException

import android.os.Build
import android.provider.Settings
import androidx.core.location.LocationManagerCompat


object Connection {


    @SuppressLint("MissingPermission")
    @Suppress("Deprecation")
    fun hasInternetConnection(context: Context):Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo?.apply {
            return isConnected && isAvailable
        }
        return false
    }






}