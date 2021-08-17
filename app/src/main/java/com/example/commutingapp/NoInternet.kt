package com.example.commutingapp

import InternetConnection.ConnectionManager
import UI.AttributesInitializer
import UI.ScreenDimension
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class NoInternet : AppCompatActivity(),AttributesInitializer {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        initializeAttributes()

    }
    override fun initializeAttributes() {
        ScreenDimension(window).setWindowToFullScreen()
        setContentView(R.layout.custom_no_internet_dialog)
    }

    fun retryButtonClicked(view: View) {
        if (ConnectionManager(this).internetConnectionAvailable()){
        finish()
        }
    }

    fun GoToSettingsClicked(view: View) {
        startActivity(Intent(Settings.ACTION_SETTINGS))
    }




}

