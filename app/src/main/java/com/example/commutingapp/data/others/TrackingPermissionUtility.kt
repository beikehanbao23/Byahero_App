package com.example.commutingapp.data.others

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import com.example.commutingapp.R
import com.example.commutingapp.data.others.Constants.REQUEST_CODE_LOCATION_PERMISSION
import pub.devrel.easypermissions.EasyPermissions


object TrackingPermissionUtility {

    fun hasLocationPermission(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }


    fun requestPermission(fragment: Fragment) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                fragment,
                fragment.getString(R.string.requestLocationMessage),
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }else{
            EasyPermissions.requestPermissions(
                fragment,
                fragment.getString(R.string.requestLocationMessage),
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    }

}