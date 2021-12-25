package com.example.commutingapp.views.ui.subComponents.fab.mapDetails

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.view.View
import com.example.commutingapp.R
import com.example.commutingapp.utils.others.Constants
import com.example.commutingapp.utils.others.SwitchState
import com.example.commutingapp.views.dialogs.CustomDialogBuilder

class MapTraffic(context: Context):IMapDetails {

    private var mapTrafficDetailsPreferences : SharedPreferences = context.getSharedPreferences(
        Constants.KEY_MAPS_TRAFFIC,
        Context.MODE_PRIVATE
    )

    override fun addMapSelectedIndicator(customDialogBuilder: CustomDialogBuilder) {
        customDialogBuilder.apply {
            if(isButtonSelected()){
                findViewById<View>( R.id.trafficMapDetailsButton)?.setBackgroundResource(R.drawable.map_type_selected_state_indicator)
            }else{
                findViewById<View>( R.id.trafficMapDetailsButton)?.setBackgroundColor(Color.WHITE)
            }
        }
    }

    override fun changeMapButtonState(state: SwitchState) {
        mapTrafficDetailsPreferences.edit().putString(Constants.KEY_MAPS_TRAFFIC,state.toString()).apply()
    }



    override fun isButtonSelected() =
        mapTrafficDetailsPreferences.getString(Constants.KEY_MAPS_TRAFFIC,SwitchState.OFF.toString()) == SwitchState.ON.toString()

}