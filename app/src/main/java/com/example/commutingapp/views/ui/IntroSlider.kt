package com.example.commutingapp.views.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.example.commutingapp.R
import com.example.commutingapp.databinding.ActivityIntroSliderBinding
import com.example.commutingapp.utils.ui_utilities.ActivitySwitcher
import com.example.commutingapp.utils.ui_utilities.AttributesInitializer
import com.example.commutingapp.utils.ui_utilities.BindingDestroyer
import com.example.commutingapp.utils.ui_utilities.ScreenDimension
import com.example.commutingapp.views.MenuButtons.CustomBackButton
import com.example.commutingapp.views.adapters.IntroSliderAdapter


const val ITEMS_COUNT = 4
const val DEFAULT_INDICATOR_POSITION = 0

class IntroSlider : AppCompatActivity(),BindingDestroyer,AttributesInitializer {

    private lateinit var preferences: SharedPreferences
    private val preferredShowIntro = "IntroSlider_StateOfSlides"
    private var binding: ActivityIntroSliderBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        initializeAttributes()

        if (userHasAlreadySeenTheIntroSliders()) {
            showSignInActivity()
            return
        }

        setupIntroSliders()
    }

    private fun setupIntroSliders(){

        provideViewPageDisplay()
        setupIndicators()

        setCurrentIndicator(DEFAULT_INDICATOR_POSITION)
        registerCallbacks()
    }
    override fun initializeAttributes() {
        ScreenDimension(window).setWindowToFullScreen()
        binding = ActivityIntroSliderBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        preferences = getSharedPreferences("IntroSlider", Context.MODE_PRIVATE)
    }

    private fun registerCallbacks(){
        with(binding?.viewPagerSliders, {
            this?.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    transitionButtonName()
                    setCurrentIndicator(position)
                }
            })
        })
    }
    private fun userHasAlreadySeenTheIntroSliders() =
        preferences.getBoolean(preferredShowIntro, false)


    private fun provideViewPageDisplay() {

        binding?.viewPagerSliders?.adapter = IntroSliderAdapter(layoutInflater,this)
    }


    private fun setupIndicators() {

        val indicators = arrayOfNulls<ImageView>(ITEMS_COUNT)
        val layoutParameters: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

        layoutParameters.setMargins(8, 0, 8, 0)
        renderIndicators(indicators, layoutParameters)

        

    }


    private fun renderIndicators(
        indicators: Array<ImageView?>,
        layoutParameters: LinearLayout.LayoutParams,
    ): Unit {

        for (counter in indicators.indices) {

            indicators[counter] = ImageView(baseContext)
            indicators[counter]?.apply {
                setInactiveIndicators(this)
                layoutParams = layoutParameters
            }
            binding?.linearLayoutDotsIndicator?.addView(indicators[counter])

        }
    }

    private fun setCurrentIndicator(index: Int) {
        val counts = binding?.linearLayoutDotsIndicator?.childCount
        for (counter in 0 until counts!!) {

            val imageView = binding?.linearLayoutDotsIndicator?.get(counter) as ImageView
            if (counter == index) setActiveIndicators(imageView) else setInactiveIndicators(
                imageView
            )
        }

    }

    private fun setActiveIndicators(imageView: ImageView) {
        imageView.setImageDrawable(
            ContextCompat.getDrawable(
                baseContext,
                R.drawable.active_indicator
            )
        )
    }


    private fun setInactiveIndicators(indicator: ImageView) {
        indicator.setImageDrawable(
            ContextCompat.getDrawable(
                baseContext,
                R.drawable.inactive_indicator
            )
        )
    }

    override fun onBackPressed() {
        CustomBackButton(this, this).applyDoubleClickToExit()

    }

    fun nextButtonSlidersIsClicked(view: View) {

        if (slideHasNext()) {
            transitionButtonName()
            moveToNextSlide()
            return
        }

        showSignInActivity()
        userIsDoneWithIntroSliders()
    }

    private fun slideHasNext() = binding!!.viewPagerSliders.currentItem < ITEMS_COUNT - 1

    private fun moveToNextSlide() {
        binding!!.viewPagerSliders.currentItem +=1

    }

    private fun slideIsLastSlide() = binding?.viewPagerSliders?.currentItem == ITEMS_COUNT - 1

    private fun transitionButtonName() {
        if (slideIsLastSlide()) binding?.nextButtonSliders?.text =
            "Let's get started!" else binding?.nextButtonSliders?.text = "Next"
    }

    private fun showSignInActivity() {
      ActivitySwitcher.startActivityOf(this,this, SignIn::class.java)
    }

    private fun userIsDoneWithIntroSliders() {

        val editor = preferences.edit()
        with(editor) {
            putBoolean(preferredShowIntro, true)
            apply()
        }
    }

    override fun onDestroy() {
        destroyBinding()
        super.onDestroy()
    }


    fun skipButtonSlidersIsClicked(view: View) {
        showSignInActivity()
        userIsDoneWithIntroSliders()
    }

    override fun destroyBinding() {
        IntroSliderAdapter(layoutInflater,this).destroyIntroSliderAdapterBinding()
        binding = null
    }



}