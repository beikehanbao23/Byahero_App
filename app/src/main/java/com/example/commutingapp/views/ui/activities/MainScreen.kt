package com.example.commutingapp.views.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.commutingapp.R
import com.example.commutingapp.databinding.ActivityMainScreenBinding
import com.example.commutingapp.utils.others.FragmentToActivity
import com.example.commutingapp.views.menubuttons.BackButton
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.location.DefaultLocationProvider
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MainScreen : AppCompatActivity(),FragmentToActivity<Fragment> {


    private var activityMainScreenBinding: ActivityMainScreenBinding? = null
    private lateinit var navigationController: NavController



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeAttributes()

        val navigationHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navigationController = navigationHostFragment.navController
        activityMainScreenBinding?.bottomNavigation?.setupWithNavController(navigationController)

        setupBottomNavigationListeners()
        initializeMapBoxSearch()

    }
    private fun initializeMapBoxSearch(){
        Mapbox.getInstance(this, getString(R.string.MapsToken))
        try {
            MapboxSearchSdk.initialize(
                this.application,
                getString(R.string.MapsToken),
                DefaultLocationProvider(this.application)
            )
        }catch (e:IllegalStateException){
            Timber.e("MapboxSearchSdk: ${e.message}")
        }
    }
    override fun onFirstNotify() {
     activityMainScreenBinding?.bottomNavigation?.visibility = View.GONE 
    }

    override fun onSecondNotify() {
        activityMainScreenBinding?.bottomNavigation?.visibility = View.VISIBLE
    }



    private fun setupBottomNavigationListeners() {

        activityMainScreenBinding?.bottomNavigation?.apply {
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.commuter_fragment -> {
                        if (currentFragment() != R.id.commuter_fragment) {
                            navigationController.navigate(R.id.main_screen_To_commuter_fragment)
                        }
                    }

                    R.id.placeBookmarks_fragment -> {
                        if(currentFragment() != R.id.placeBookmarks_fragment){
                            navigationController.navigate(R.id.main_screen_To_place_bookmarks_fragment)
                        }
                    }

                    R.id.weather_fragment -> {
                        if (currentFragment() != R.id.weather_fragment) {
                           navigationController.navigate(R.id.main_screen_To_weather_fragment)
                        }
                    }
                }
                true
            }
        }
    }



    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)


    }




    private fun currentFragment(): Int? {
        return navigationController.currentDestination?.id
    }
    override fun onBackPressed() {
        if(currentFragment() == R.id.commuter_fragment){
            BackButton().applyDoubleClickToExit(this)
            return
        }

        try { super.onBackPressed() }catch (e:Exception){ }
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyBinding()
    }


    private fun initializeAttributes() {
        activityMainScreenBinding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(activityMainScreenBinding?.root)

    }

    private fun destroyBinding() {
        activityMainScreenBinding = null
    }





}