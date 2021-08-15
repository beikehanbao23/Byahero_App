package com.example.commutingapp

import UI.ScreenDimension
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class TokenExpired : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenDimension(window).setWindowToFullScreen()
        setContentView(R.layout.activity_token_expired)
    }

    fun OkButtonIsClicked(view: View) {
        //startActivity(Intent(this, SignIn::class.java))
       // finish()

    }
}