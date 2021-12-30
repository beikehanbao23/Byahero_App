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
import com.example.commutingapp.data.firebase.auth.FirebaseAuthenticatorWrapper
import com.example.commutingapp.data.firebase.auth.UserAuthenticationProcessor
import com.example.commutingapp.data.firebase.usr.FirebaseUserWrapper
import com.example.commutingapp.data.firebase.usr.UserDataProcessor
import com.example.commutingapp.data.firebase.usr.UserEmailProcessor
import com.example.commutingapp.databinding.ActivityMainScreenBinding
import com.example.commutingapp.utils.others.FragmentToActivity
import com.example.commutingapp.utils.ui_utilities.ActivitySwitch
import com.example.commutingapp.views.menubuttons.BackButton
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserInfo
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.search.MapboxSearchSdk
import com.mapbox.search.location.DefaultLocationProvider
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*


@AndroidEntryPoint
class MainScreen : AppCompatActivity(),FragmentToActivity<Fragment> {
    private val firebaseUser = FirebaseUserWrapper()
    private val userData: UserDataProcessor<List<UserInfo>?> = UserDataProcessor(firebaseUser)
    private val userEmail: UserEmailProcessor<Task<Void>?> = UserEmailProcessor(firebaseUser)
    private val userAuthentication: UserAuthenticationProcessor<Task<AuthResult>> = UserAuthenticationProcessor(FirebaseAuthenticatorWrapper())
    private var activityMainScreenBinding: ActivityMainScreenBinding? = null
    private lateinit var navigationController: NavController
    private var isCommuterFragmentAtForeground = false


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
                    R.id.settings_fragment -> {
                        if (currentFragment() != R.id.settings_fragment) {
                            navigationController.navigate(R.id.main_screen_To_settings_fragment)
                        }
                    }
                    R.id.statistics_fragment -> {
                        if (currentFragment() != R.id.statistics_fragment) {
                            navigationController.navigate(R.id.main_screen_To_statistics_fragment)
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

    override fun onStart() {
        super.onStart()
        displayUserProfileName()
    }

    private fun initializeAttributes() {
        activityMainScreenBinding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(activityMainScreenBinding?.root)

    }

    private fun destroyBinding() {
        activityMainScreenBinding = null
    }


    private val userProfileName: String?
        get() {
            for (user in userData.getUserProviderData()!!) {
                if (userSignInViaFacebookUsing(user.providerId) || userSignInViaGoogleUsing(user.providerId)) {
                    return userData.getDisplayName()
                }
            }
            return filterEmailAddress(userEmail.getUserEmail())
        }

    private fun userSignInViaFacebookUsing(userProviderId: String) =
        userProviderId == FacebookAuthProvider.PROVIDER_ID

    private fun userSignInViaGoogleUsing(userProviderId: String) =
        userProviderId == GoogleAuthProvider.PROVIDER_ID


    private fun displayUserProfileName() {
        //TODO activityMainScreenBinding?.nameTextView?.text = userProfileName

    }

    private fun filterEmailAddress(userEmail: String?): String? {
        userEmail?.let {
            emailExtensions.forEach { emailExtensions ->
                if (userEmail.contains(emailExtensions)) {
                    return userEmail.replace(emailExtensions.toRegex(), "")
                }
            }
        }
        return userEmail
    }

    private val emailExtensions: List<String>
        get() {
            val list: MutableList<String> = ArrayList()
            list.add("@gmail.com")
            list.add("@protonmail.ch")
            list.add("@yahoo.com")
            list.add("@hotmail.com")
            list.add("@outlook.com")
            return list
        }

    //
    fun logoutButtonIsClicked(view: View?) {
        //TODO putToLoginFlow()
    }

    private fun signOutAccount() {
        userAuthentication.signOut()
    }

    private fun putToLoginFlow() {
        signOutAccount()
        showSignInActivity()
    }

    private fun showSignInActivity() {

        ActivitySwitch.startActivityOf(this,  SignIn::class.java)
    }




}