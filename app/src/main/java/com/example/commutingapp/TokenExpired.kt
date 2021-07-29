package com.example.commutingapp

import Screen.ScreenDimension
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class TokenExpired : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenDimension(window).windowToFullScreen()
        setContentView(R.layout.activity_token_expired)
    }
}