package com.example.commutingapp.utils.ui_utilities

import android.view.Window
import android.view.WindowManager

/**
 * Manages the screen resolution
 */


object ScreenDimension{


     fun setWindowToFullScreen(window:Window){
        @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }



    }
