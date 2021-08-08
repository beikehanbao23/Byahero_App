package com.example.commutingapp

import  Adapters.IntroSliderAdapter
import Logger.CustomToastMessage
import MenuButtons.CustomBackButton
import MenuButtons.backButton
import Screen.ScreenDimension
import android.content.Context
import android.content.Intent
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
import com.example.commutingapp.databinding.ActivityIntroSliderBinding


const val ITEMS_COUNT = 4
const val DEFAULT_INDICATOR_POSITION = 0

class IntroSlider : AppCompatActivity() {

    private lateinit var toastMessageBackButton: CustomToastMessage
    private lateinit var preferences: SharedPreferences
    private val preferedShowIntro = "IntroSlider_StateOfSlides"
    private var binding: ActivityIntroSliderBinding? = null
    private lateinit var introSliderAdapter: IntroSliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        ScreenDimension(window).windowToFullScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityIntroSliderBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        preferences = getSharedPreferences("IntroSlider", Context.MODE_PRIVATE)
        introSliderAdapter = IntroSliderAdapter(layoutInflater,this)
        if (userHasAlreadySeenTheIntroSliders()) {
            showSignInForm()
            return
        }

        toastMessageBackButton =
            CustomToastMessage(this, getString(R.string.doubleTappedMessage), 10)
        setupIntroSlidersAttributes()
        setupIntroSliderPageIndicators()

        setCurrentIndicator(DEFAULT_INDICATOR_POSITION)
        implementViewPagerSlideActionCallback()
    }

    private fun implementViewPagerSlideActionCallback(){
        with(binding?.viewPagerSliders, {
            this?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    transitionButtonName()
                    setCurrentIndicator(position)
                }
            })
        })
    }
    private fun userHasAlreadySeenTheIntroSliders() =
        preferences.getBoolean(preferedShowIntro, false)

    private fun setupIntroSlidersAttributes() {

        binding?.viewPagerSliders?.adapter = introSliderAdapter
    }

    private fun setupIntroSliderPageIndicators() {
        val indicators = arrayOfNulls<ImageView>(ITEMS_COUNT)
        val layoutParameters: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

        layoutParameters.setMargins(8, 0, 8, 0)
        renderIndicators(indicators, layoutParameters)

        

    }


    private fun renderIndicators(
        indicators: Array<ImageView?>,
        layoutParameters: LinearLayout.LayoutParams
    ): Unit {

        for (counter in indicators.indices) {

            indicators[counter] = ImageView(applicationContext)
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
                applicationContext,
                R.drawable.active_indicator
            )
        )
    }


    private fun setInactiveIndicators(indicator: ImageView) {
        indicator.setImageDrawable(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.inactive_indicator
            )
        )
    }

    override fun onBackPressed() {
        CustomBackButton {
            if (backButton.isDoubleTapped()) {
                toastMessageBackButton.hideToast()
                super.onBackPressed()
                return@CustomBackButton
            }
            toastMessageBackButton.showToast()
            backButton.registerFirstClick()
        }.backButtonIsClicked()
    }

    fun nextButtonSlidersIsClicked(view: View) {

        if (slideHasNext()) {
            transitionButtonName()
            moveToNextSlide()
            return
        }

        showSignInForm()
        userIsDoneWithIntroSliders()
    }

    private fun slideHasNext() = binding?.viewPagerSliders?.currentItem!! < ITEMS_COUNT - 1
    private fun moveToNextSlide() {
        binding?.viewPagerSliders?.currentItem?.inc()
    }

    private fun slideIsLastSlide() = binding?.viewPagerSliders?.currentItem == ITEMS_COUNT - 1

    private fun transitionButtonName() {
        if (slideIsLastSlide()) binding?.nextButtonSliders?.text =
            "Let's get started!" else binding?.nextButtonSliders?.text = "Next"
    }

    private fun showSignInForm() {
        startActivity(Intent(this, SignIn::class.java))
        finish()
    }

    private fun userIsDoneWithIntroSliders() {

        val editor = preferences.edit()
        with(editor) {
            putBoolean(preferedShowIntro, true)
            apply()
        }
    }

    override fun onDestroy() {
        introSliderAdapter.destroyIntroSliderAdapterBinding()
        binding = null
        super.onDestroy()
    }


    fun skipButtonSlidersIsClicked(view: View) {
        showSignInForm()
        userIsDoneWithIntroSliders()
    }

}