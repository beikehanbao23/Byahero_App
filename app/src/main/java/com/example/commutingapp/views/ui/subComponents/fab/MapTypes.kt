package com.example.commutingapp.views.ui.subComponents.fab

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.View
import com.example.commutingapp.BuildConfig
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.Constants.KEY_MAPS_TYPE
import com.example.commutingapp.views.dialogs.CustomDialogBuilder
import com.mapbox.mapboxsdk.maps.Style

class MapTypes(private val context: Context) {
    private var mapTypesPreferences: SharedPreferences = context.getSharedPreferences(
        KEY_MAPS_TYPE,
        Context.MODE_PRIVATE)



     fun setMapSelectedIndicator(customDialogBuilder: CustomDialogBuilder){
        customDialogBuilder.also {
            val mapTypeButton = getMapTypeButtons().getValue(currentMapType())
            it.findViewById<View>(mapTypeButton)?.setBackgroundResource(R.drawable.map_type_selected_state_indicator)
        }
    }

    private fun removePreviousMapTypeIndicator(customDialogBuilder: CustomDialogBuilder){
        customDialogBuilder.apply {
            for(i in getMapTypeButtons()){
                if(i.key == currentMapType()){
                    continue
                }else{
                    findViewById<View>(i.value)?.setBackgroundColor(Color.WHITE)
                }
            }
        }
    }

     fun getMapTypeButtons():HashMap<String,Int> =
        HashMap<String,Int>().apply {
            this[Style.LIGHT] = R.id.defaultMapStyleButton
            this[Style.DARK] = R.id.darkMapStyleButton
            this[Style.MAPBOX_STREETS] = R.id.streetMapStyleButton
            this[Style.SATELLITE_STREETS] = R.id.satelliteStreetsMapStyleButton
            this[BuildConfig.MAP_STYLE] = R.id.neonMapStyleButton
            this[Style.SATELLITE] = R.id.satelliteMapStyleButton

        }



     fun changeMapType(customDialogBuilder: CustomDialogBuilder,mapType: String) {
        saveMapTypeToSharedPreference(mapType)
        removePreviousMapTypeIndicator(customDialogBuilder)
        setMapSelectedIndicator(customDialogBuilder)
    }

     fun currentMapType():String = mapTypesPreferences.getString(KEY_MAPS_TYPE,Style.LIGHT).toString()

    private fun saveMapTypeToSharedPreference(mapType: String) {
        mapTypesPreferences.edit().putString(KEY_MAPS_TYPE, mapType).apply()
    }


}
