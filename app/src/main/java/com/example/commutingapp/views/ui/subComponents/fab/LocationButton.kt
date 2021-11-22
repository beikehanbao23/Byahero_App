package com.example.commutingapp.views.ui.subComponents.fab

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.widget.ImageViewCompat
import com.example.commutingapp.R
import com.example.commutingapp.databinding.CommuterFragmentBinding
import com.example.commutingapp.utils.InternetConnection.Connection

class LocationButton(private val commuterFragmentBinding: CommuterFragmentBinding, private val context:Context) {

     fun showLocationFloatingButton(){
        commuterFragmentBinding.floatingActionButtonLocation.visibility = View.VISIBLE
    }
     fun hideLocationFloatingButton(){
        commuterFragmentBinding.floatingActionButtonLocation.visibility = View.GONE
    }
    @RequiresApi(Build.VERSION_CODES.M)
     fun updateLocationFloatingButtonIcon()=
        if(Connection.hasGPSConnection(context)) changeFloatingButtonIconBlack() else changeFloatingButtonIconRed()

     private fun changeFloatingButtonIconRed(){
        changeLocationFloatingButtonIconColor(Color.RED)
        changeLocationFloatingButtonIcon(R.drawable.ic_location_asking)
    }
     fun changeFloatingButtonIconBlack(){
        changeLocationFloatingButtonIconColor(Color.BLACK)
        changeLocationFloatingButtonIcon(R.drawable.ic_baseline_my_location)
    }
     fun changeFloatingButtonIconBlue(){
        changeLocationFloatingButtonIconColor(Color.BLUE)
        changeLocationFloatingButtonIcon(R.drawable.ic_baseline_my_location)
    }
     private fun changeLocationFloatingButtonIconColor(@ColorInt color:Int){
        ImageViewCompat.setImageTintList(
            commuterFragmentBinding.floatingActionButtonLocation,
            ColorStateList.valueOf(color))

    }
    private fun changeLocationFloatingButtonIcon(@DrawableRes imageId:Int){
        commuterFragmentBinding.floatingActionButtonLocation.setImageResource(imageId)
    }
}