package com.example.commutingapp.views.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.commutingapp.R
import com.example.commutingapp.utils.InternetConnection.ConnectionManager
import com.example.commutingapp.utils.ui_utilities.AttributesInitializer
import com.example.commutingapp.utils.ui_utilities.ScreenDimension

class NoInternet : AppCompatActivity(),AttributesInitializer {


    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO add coroutine
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

