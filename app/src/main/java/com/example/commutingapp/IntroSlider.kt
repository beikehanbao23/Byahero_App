package com.example.commutingapp

import Adapters.IntroSliderAdapter
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_intro_slider.*

class IntroSlider : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_slider)
        viewPagerSliders.adapter = IntroSliderAdapter(this)


    }

    private fun setupPageIndicators() {
        val indicators = arrayOfNulls<ImageView>(3)
        val layoutParameters: LinearLayout.LayoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParameters.setMargins(8,0,8,0)

        for(counter in indicators.indices){

            indicators[counter] = ImageView(applicationContext)
            indicators[counter].apply {
                this?.setImageDrawable(ContextCompat.getDrawable(applicationContext,R.drawable.inactive_indicator))
                this?.layoutParams = layoutParameters
            }
            linearLayout_dotsIndicator.addView(indicators[counter])
        }

    }
}