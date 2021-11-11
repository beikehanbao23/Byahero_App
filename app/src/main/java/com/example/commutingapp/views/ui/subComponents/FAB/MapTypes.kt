package com.example.commutingapp.views.ui.subComponents.FAB

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.View
import com.example.commutingapp.BuildConfig
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.Constants.FILE_NAME_MAPS_TYPE_SHARED_PREFERENCE
import com.example.commutingapp.views.dialogs.CustomDialogBuilder
import com.mapbox.mapboxsdk.maps.Style

class MapTypes(private val context: Context) {
    private var preferences: SharedPreferences = context.getSharedPreferences(
        FILE_NAME_MAPS_TYPE_SHARED_PREFERENCE,
        Context.MODE_PRIVATE)


     fun createMapTypeIndicator(customDialogBuilder: CustomDialogBuilder){
        customDialogBuilder.apply {
            findViewById<View>(getMapTypeButtons().getValue(loadMapType()))?.setBackgroundResource(R.drawable.map_type_visible_image_button_background)
        }
    }

    private fun removePreviousMapTypeIndicator(customDialogBuilder: CustomDialogBuilder){
        customDialogBuilder.apply {
            for(i in getMapTypeButtons()){
                if(i.key == loadMapType()){
                    continue
                }else{
                    findViewById<View>(i.value)?.setBackgroundColor(Color.WHITE)
                }
            }
        }
    }

     fun getMapTypeButtons():HashMap<String,Int> =
        HashMap<String,Int>().apply {
            this[Style.TRAFFIC_DAY] = R.id.defaultMapStyleButton
            this[Style.TRAFFIC_NIGHT] = R.id.trafficNightMapStyleButton
            this[Style.DARK] = R.id.darkMapStyleButton
            this[Style.MAPBOX_STREETS] = R.id.streetMapStyleButton
            this[Style.SATELLITE_STREETS] = R.id.satelliteStreetsMapStyleButton
            this[Style.OUTDOORS] = R.id.outdoorsMapStyleButton
            this[Style.LIGHT] = R.id.lightMapStyle
            this[BuildConfig.MAP_STYLE] = R.id.neonMapStyleButton
            this[Style.SATELLITE] = R.id.satelliteMapStyleButton

        }

     fun changeMapType(customDialogBuilder: CustomDialogBuilder,mapType: String) {
        saveMapTypeToSharedPreference(mapType)
        removePreviousMapTypeIndicator(customDialogBuilder)
        createMapTypeIndicator(customDialogBuilder)
    }

     fun loadMapType():String {return preferences.getString(FILE_NAME_MAPS_TYPE_SHARED_PREFERENCE,Style.TRAFFIC_DAY).toString()}

    private fun saveMapTypeToSharedPreference(mapType:String){
        preferences.edit().apply{
            putString(FILE_NAME_MAPS_TYPE_SHARED_PREFERENCE,mapType)
            apply()
        }
    }




}
