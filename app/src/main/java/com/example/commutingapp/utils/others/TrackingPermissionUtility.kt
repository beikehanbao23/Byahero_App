package com.example.commutingapp.utils.others

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.Constants.REQUEST_CODE_AUDIO_RECORD_PERMISSION
import com.example.commutingapp.utils.others.Constants.REQUEST_CODE_LOCATION_PERMISSION
import pub.devrel.easypermissions.EasyPermissions


object TrackingPermissionUtility {

    fun hasLocationPermission(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }


    fun requestLocationPermission(fragment: Fragment) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                fragment,
                fragment.getString(R.string.requestLocationMessage),
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }else{
            EasyPermissions.requestPermissions(
                fragment,
                fragment.getString(R.string.requestLocationMessage),
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

    }




    fun hasRecordAudioPermission(context:Context) =
        EasyPermissions.hasPermissions(context, Manifest.permission.RECORD_AUDIO)

    fun requestRecordAudioPermission(fragment: Fragment) {
        EasyPermissions.requestPermissions(
            fragment,
            fragment.getString(R.string.requestAudioRecord),
            REQUEST_CODE_AUDIO_RECORD_PERMISSION,
            Manifest.permission.RECORD_AUDIO
        )
    }


}