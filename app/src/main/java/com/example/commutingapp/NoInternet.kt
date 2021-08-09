package com.example.commutingapp

import InternetConnection.ConnectionManager
import Screen.ScreenDimension
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class NoInternet : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        ScreenDimension(window).windowToFullScreen()
        setContentView(R.layout.custom_no_internet_dialog)
    }

    override fun onStart() {
        super.onStart()

    }

    fun retryButtonClicked(view: View) {
        if (!ConnectionManager(this).PhoneHasInternetConnection()){
        finish()
        }
    }

    fun GoToSettingsClicked(view: View) {
        startActivity(Intent(Settings.ACTION_SETTINGS))
    }


}

