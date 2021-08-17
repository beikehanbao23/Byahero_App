package com.example.commutingapp

import UI.AttributesInitializer
import UI.BindingDestroyer
import UI.ScreenDimension
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.commutingapp.databinding.ActivityTokenExpiredBinding

class TokenExpired : AppCompatActivity(),BindingDestroyer, AttributesInitializer {

    private var binding:ActivityTokenExpiredBinding? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeAttributes()
    }


    fun OkButtonIsClicked(view: View) {}

    override fun onDestroy() {
        destroyBinding()
        super.onDestroy()
    }

    override fun destroyBinding() {
        binding = null

    }

    override fun initializeAttributes() {
        ScreenDimension(window).setWindowToFullScreen()
        binding = ActivityTokenExpiredBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }


}