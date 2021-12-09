package com.example.commutingapp.views.ui.subComponents.fab.mapDetails

import com.example.commutingapp.utils.others.SwitchState
import com.example.commutingapp.views.dialogs.CustomDialogBuilder

interface IMapDetails {

    fun addMapSelectedIndicator(customDialogBuilder: CustomDialogBuilder)
    fun changeMapButtonState(state:SwitchState)
    fun isButtonSelected():Boolean
}