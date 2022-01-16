package com.example.commutingapp.views.ui.subComponents

import android.content.Context
import android.view.View
import android.widget.Button
import com.example.commutingapp.R
import com.example.commutingapp.utils.InternetConnection.Connection
import com.google.android.material.bottomsheet.BottomSheetBehavior

class StartingBottomSheet(view:View, private val context: Context) :IComponent{
    private  var startingBottomSheet: BottomSheetBehavior<View> = BottomSheetBehavior.from(view.findViewById(R.id.bottomSheetNormalState)).apply {
        this.isHideable = true
    }


    private var startButton: Button = view.findViewById(R.id.startButton)
    private var directionButton: Button = view.findViewById(R.id.directionsButton)
    private var saveButton: Button = view.findViewById(R.id.saveButton)


    override fun hide(){
        startingBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }
    override fun show() {
        renderBottomSheetButtons()
        startingBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }







    private fun renderBottomSheetButtons(){
        if(!Connection.hasInternetConnection(context) && Connection.hasGPSConnection(context)){
            showNoInternetAndHasGpsBottomSheetLayout()
            return
        }
        if(Connection.hasInternetConnection(context) && !Connection.hasGPSConnection(context)){
            showHasInternetAndNoGpsBottomSheetLayout()
            return
        }
        if(!Connection.hasInternetConnection(context) && !Connection.hasGPSConnection(context)){
            showNoInternetAndNoGpsBottomSheetLayout()
            return
        }

        showDefaultBottomSheetLayout()
    }
    private fun showNoInternetAndHasGpsBottomSheetLayout() {
        saveButton.visibility = View.GONE
        directionButton.visibility = View.VISIBLE
        startButton.visibility = View.VISIBLE
    }
    private fun showHasInternetAndNoGpsBottomSheetLayout() {
        startButton.visibility = View.GONE
        saveButton.visibility = View.VISIBLE
        directionButton.visibility = View.VISIBLE
    }
    private fun showNoInternetAndNoGpsBottomSheetLayout() {
        startButton.visibility = View.GONE
        saveButton.visibility = View.GONE
        directionButton.visibility = View.VISIBLE
    }
    private fun showDefaultBottomSheetLayout() {
        saveButton.visibility = View.VISIBLE
        directionButton.visibility = View.VISIBLE
        startButton.visibility = View.VISIBLE

    }


}