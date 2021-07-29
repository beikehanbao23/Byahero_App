package Screen

import android.os.Build
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager

/**
 * Manages the screen resolution
 */
class ScreenDimension(var window: Window){


    public fun windowToFullScreen(){
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }
}