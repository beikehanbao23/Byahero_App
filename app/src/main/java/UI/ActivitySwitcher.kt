package UI

import android.app.Activity
import android.content.Context
import android.content.Intent

object ActivitySwitcher {
    /**
     * Start new activity and closing the previous activity, popping the previous activity at the backstack
     */
    fun startActivityOf(activityOfThisClass: Activity ,contextOfThisClass: Context, classToOpen:Class<*>){

        contextOfThisClass.startActivity(Intent(contextOfThisClass,classToOpen))
        activityOfThisClass.finish()
    }
    /**
     * Start new activity without closing the previous activity, previous activity remains at the backstack
     */

    fun startActivityOf(contextOfThisClass: Context, classToOpen:Class<*>){

        contextOfThisClass.startActivity(Intent(contextOfThisClass,classToOpen))
    }
}
