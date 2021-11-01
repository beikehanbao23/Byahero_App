package com.example.commutingapp.views.ui.subComponents

import android.content.Context
import android.view.View
import android.widget.Button
import com.example.commutingapp.R
import com.example.commutingapp.utils.InternetConnection.Connection
import com.example.commutingapp.utils.others.Constants
import com.google.android.material.bottomsheet.BottomSheetBehavior

class StartingBottomSheet(view:View, private val context: Context) :IComponent{
    private  var startingBottomSheet: BottomSheetBehavior<View> = BottomSheetBehavior.from(view.findViewById(R.id.bottomSheetNormalState)).apply {
        state = BottomSheetBehavior.STATE_COLLAPSED
    }
    private var startButton: Button = view.findViewById(R.id.startButton)
    private var directionButton: Button = view.findViewById(R.id.directionsButton)
    private var saveButton: Button = view.findViewById(R.id.saveButton)
    private var shareButton: Button = view.findViewById(R.id.shareButton)


    override fun hide(){
        startingBottomSheet.peekHeight = Constants.INVISIBLE_BOTTOM_SHEET_PEEK_HEIGHT
    }
    override fun show() {
        renderBottomSheetButtons()
        startingBottomSheet.peekHeight = Constants.STARTING_VISIBLE_BOTTOM_SHEET_PEEK_HEIGHT
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
        shareButton.visibility = View.GONE
        saveButton.visibility = View.GONE
        directionButton.visibility = View.VISIBLE
        startButton.visibility = View.VISIBLE
    }
    private fun showHasInternetAndNoGpsBottomSheetLayout() {
        startButton.visibility = View.GONE
        saveButton.visibility = View.VISIBLE
        shareButton.visibility = View.VISIBLE
        directionButton.visibility = View.VISIBLE
    }
    private fun showNoInternetAndNoGpsBottomSheetLayout() {
        startButton.visibility = View.GONE
        saveButton.visibility = View.GONE
        shareButton.visibility = View.GONE
        directionButton.visibility = View.VISIBLE
    }
    private fun showDefaultBottomSheetLayout() {
        saveButton.visibility = View.VISIBLE
        shareButton.visibility = View.VISIBLE
        directionButton.visibility = View.VISIBLE
        startButton.visibility = View.VISIBLE

    }


}