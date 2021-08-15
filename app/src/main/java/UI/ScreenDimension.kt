package UI

import android.view.Window
import android.view.WindowManager

/**
 * Manages the screen resolution
 */
class ScreenDimension(var window: Window){


     fun setWindowToFullScreen(){
        @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }



    }
