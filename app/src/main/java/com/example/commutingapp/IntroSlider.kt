package com.example.commutingapp

import Adapters.IntroSliderAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_intro_slider.*

class IntroSlider : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_slider)
        viewPagerSliders.adapter = IntroSliderAdapter(this)


    }

}