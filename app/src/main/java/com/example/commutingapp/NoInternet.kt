package com.example.commutingapp

import InternetConnection.ConnectionManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class NoInternet : AppCompatActivity() {

    private lateinit var connectionManager: ConnectionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        windowToFullScreen()
        setContentView(R.layout.custom_no_internet_dialog)
    }

    private fun windowToFullScreen(){
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
    override fun onStart() {
        super.onStart()
        connectionManager = ConnectionManager(this)
    }

    fun retryButtonClicked(view: View) {
        if (connectionManager.PhoneHasInternetConnection()){
        finish()
        }
    }

    fun GoToSettingsClicked(view: View) {
        startActivity(Intent(Settings.ACTION_SETTINGS))
    }


}

