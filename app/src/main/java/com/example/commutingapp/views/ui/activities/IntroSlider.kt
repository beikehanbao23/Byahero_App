package com.example.commutingapp.views.ui.activities

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
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.commutingapp.R
import com.example.commutingapp.databinding.ActivityIntroSliderBinding
import com.example.commutingapp.utils.others.Constants.DEFAULT_INDICATOR_POSITION
import com.example.commutingapp.utils.others.Constants.KEY_INTRO_SLIDER
import com.example.commutingapp.utils.others.Constants.SLIDER_ITEM_COUNTS
import com.example.commutingapp.utils.ui_utilities.ActivitySwitch.startActivityOf
import com.example.commutingapp.utils.ui_utilities.ScreenDimension
import com.example.commutingapp.viewmodels.IntroSliderViewModel
import com.example.commutingapp.views.adapters.IntroSliderAdapter
import com.example.commutingapp.views.menubuttons.BackButton




class IntroSlider : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences
    private var binding: ActivityIntroSliderBinding? = null
    private lateinit var viewModel: IntroSliderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        initializeAttributes()

        viewModel = ViewModelProvider(this)[IntroSliderViewModel::class.java]
        if (userHasAlreadySeenTheIntroSliders()) {
            startTransitionToNextActivity()
            return
        }
        setupIntroSliders()
    }


    private fun startTransitionToNextActivity() {
        viewModel.setUserSignInProvider()
        viewModel.navigateToDetailsOnSuccess().observe(this) {

            it.getContentIfNotHandled()?.let {
                showMainScreenActivity()
            }

        }


        if (viewModel.navigateToDetailsOnSuccess().value == null) {
            showSignInActivity()
        }
    }

    private fun showMainScreenActivity() {
        startActivityOf(this, MainScreen::class.java)
    }


    private fun setupIntroSliders() {

        setViewPageDisplay()
        setupIndicators()

        setCurrentIndicator(DEFAULT_INDICATOR_POSITION)
        setCallbacks()
    }

    private fun initializeAttributes() {
        ScreenDimension.setWindowToFullScreen(window)
        binding = ActivityIntroSliderBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_CommutingApp)
        setContentView(binding?.root)
        preferences = getSharedPreferences(KEY_INTRO_SLIDER, Context.MODE_PRIVATE)
    }

    private fun setCallbacks() {
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
        preferences.getBoolean(KEY_INTRO_SLIDER, false)


    private fun setViewPageDisplay() {

        binding?.viewPagerSliders?.adapter = IntroSliderAdapter(layoutInflater, this)
    }


    private fun setupIndicators() {

        val indicators = arrayOfNulls<ImageView>(SLIDER_ITEM_COUNTS)
        val layoutParameters: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

        layoutParameters.setMargins(8, 0, 8, 0)
        renderIndicators(indicators, layoutParameters)


    }


    private fun renderIndicators(
        indicators: Array<ImageView?>,
        layoutParameters: LinearLayout.LayoutParams,
    ) {

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
        BackButton().applyDoubleClickToExit(this)

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

    private fun slideHasNext() = binding!!.viewPagerSliders.currentItem < SLIDER_ITEM_COUNTS - 1

    private fun moveToNextSlide() {
        binding!!.viewPagerSliders.currentItem += 1

    }

    private fun slideIsLastSlide() = binding?.viewPagerSliders?.currentItem == SLIDER_ITEM_COUNTS - 1

    private fun transitionButtonName() {
        if (slideIsLastSlide()) binding?.nextButtonSliders?.text =
            getString(R.string.letsGetStarted) else binding?.nextButtonSliders?.text = getString(R.string.nextButton)
    }

    private fun showSignInActivity() {
        startActivityOf(this, SignIn::class.java)
    }

    private fun userIsDoneWithIntroSliders() {

         preferences.edit().apply {
            putBoolean(KEY_INTRO_SLIDER, true)
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

    private fun destroyBinding() {
        IntroSliderAdapter(layoutInflater, this).destroyIntroSliderAdapterBinding()
        binding = null

    }


}