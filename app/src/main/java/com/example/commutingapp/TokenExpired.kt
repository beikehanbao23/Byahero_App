package com.example.commutingapp

import Screen.ScreenDimension
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class TokenExpired : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenDimension(window).windowToFullScreen()
        setContentView(R.layout.activity_token_expired)
    }

    fun OkButtonIsClicked(view: View) {
        startActivity(Intent(this, SignIn::class.java))
        finish()
    }
}