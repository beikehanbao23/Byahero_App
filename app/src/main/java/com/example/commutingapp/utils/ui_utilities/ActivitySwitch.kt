package com.example.commutingapp.utils.ui_utilities

import android.app.Activity
import android.content.Context
import android.content.Intent

object ActivitySwitch {
    /**
     * Start new activity and closing the previous activity, popping the previous activity at the backstack
     */
    fun startActivityOf(activity: Activity, classToOpen:Class<*>){

        activity.startActivity(Intent(activity,classToOpen))
        activity.finish()
    }


}
