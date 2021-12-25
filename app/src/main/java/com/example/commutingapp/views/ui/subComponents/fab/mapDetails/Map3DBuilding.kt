package com.example.commutingapp.views.ui.subComponents.fab.mapDetails

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.View
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.Constants
import com.example.commutingapp.utils.others.SwitchState
import com.example.commutingapp.views.dialogs.CustomDialogBuilder

class Map3DBuilding(context: Context):IMapDetails {

    private var map3dDetailsPreferences: SharedPreferences = context.getSharedPreferences(
        Constants.KEY_MAPS_3D,
        Context.MODE_PRIVATE)



    override fun addMapSelectedIndicator(customDialogBuilder: CustomDialogBuilder) {
        customDialogBuilder.apply {
            if (isButtonSelected()) {
                findViewById<View>(R.id.maps3dDetailsButton)
                    ?.setBackgroundResource(R.drawable.map_type_selected_state_indicator)
            } else {
                findViewById<View>(R.id.maps3dDetailsButton)?.setBackgroundColor(Color.WHITE)
            }
        }
    }

    override fun changeMapButtonState(state: SwitchState) {
        map3dDetailsPreferences.edit().putString(Constants.KEY_MAPS_3D,state.toString()).apply()
    }



    override fun isButtonSelected() =
        map3dDetailsPreferences.getString(Constants.KEY_MAPS_3D,SwitchState.OFF.toString()) == SwitchState.ON.toString()

}