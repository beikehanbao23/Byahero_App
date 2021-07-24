package com.example.commutingapp

import Adapters.IntroSliderAdapter
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_intro_slider.*

class IntroSlider : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_slider)

        setupIntroSliders()
        setupIntroSliderPageIndicators()

        setCurrentIndicator(0)
    }

    private fun setupIntroSliders(){
        viewPagerSliders.adapter = IntroSliderAdapter(this)
    }

        private fun setupIntroSliderPageIndicators() {
            val indicators = arrayOfNulls<ImageView>(4)
            val layoutParameters: LinearLayout.LayoutParams =
                LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

            layoutParameters.setMargins(8, 0, 8, 0)
            renderIndicators(indicators,layoutParameters)


        /*
            also - add more, extension
            apply - use attributes of object
            let - null checks
            with - implicit object names
            run - to implicit this and return type R

         */

        }

    override fun onBackPressed() {

    }

    private fun renderIndicators(indicators: Array<ImageView?>, layoutParameters: LinearLayout.LayoutParams):Unit{

    for (counter in indicators.indices) {

        indicators[counter] = ImageView(applicationContext)
        indicators[counter]?.apply {
            setInactiveIndicators(this)
            layoutParams = layoutParameters
        }
        linearLayout_dotsIndicator.addView(indicators[counter])

    }
}
    private fun setCurrentIndicator(index:Int) {
        val counts = linearLayout_dotsIndicator.childCount
        for (counter in 0 until counts) {
            val imageView = linearLayout_dotsIndicator[counter] as ImageView
            if(counter == index) setActiveIndicators(imageView) else setInactiveIndicators(imageView)
        }

    }

    private fun setActiveIndicators(imageView:ImageView){
        imageView.setImageDrawable(ContextCompat.getDrawable(
            applicationContext,
            R.drawable.active_indicator))
    }


    private fun setInactiveIndicators(indicator:ImageView){
    indicator.setImageDrawable(ContextCompat.getDrawable(
        applicationContext,
        R.drawable.inactive_indicator))
    }


}