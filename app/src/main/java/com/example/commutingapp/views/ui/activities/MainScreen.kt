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
import com.example.commutingapp.utils.others.Constants.ACTION_SHOW_COMMUTER_FRAGMENT
import com.example.commutingapp.utils.others.Constants.KEY_DESTINATION_LATITUDE
import com.example.commutingapp.utils.others.Constants.KEY_DESTINATION_LONGITUDE
import com.example.commutingapp.utils.others.Constants.KEY_LAST_LOCATION_LATITUDE
import com.example.commutingapp.utils.others.Constants.KEY_LAST_LOCATION_LONGITUDE
import com.example.commutingapp.utils.others.FragmentToActivity
import com.example.commutingapp.utils.ui_utilities.ActivitySwitch
import com.example.commutingapp.views.menubuttons.NavigationButton
import com.example.commutingapp.views.ui.fragments.CommuterFragment
import com.example.commutingapp.views.ui.fragments.SettingsFragment
import com.example.commutingapp.views.ui.fragments.StatisticsFragment
import com.example.commutingapp.views.ui.fragments.WeatherFragment
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserInfo
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import dagger.hilt.android.AndroidEntryPoint
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
        navigateToCommuterFragment(intent)
        val navigationHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navigationController = navigationHostFragment.navController
        activityMainScreenBinding?.bottomNavigation?.setupWithNavController(navigationController)
        setupBottomNavigationListeners()
        Mapbox.getInstance(this, getString(R.string.MapsToken))


    }

    override fun onFirstNotify() {
     activityMainScreenBinding?.bottomNavigation?.visibility = View.GONE
    }

    override fun onSecondNotify() {
        activityMainScreenBinding?.bottomNavigation?.visibility = View.VISIBLE
    }

    override fun onThirdNotify(fragment: Fragment, destination: LatLng?, lastKnownLocation: LatLng?) {


        destination?.let { destinationLocation->
            lastKnownLocation?.let { lastLocation->
                with(Bundle()){
                    putDouble(KEY_DESTINATION_LATITUDE, destinationLocation.latitude)
                    putDouble(KEY_DESTINATION_LONGITUDE, destinationLocation.longitude)
                    putDouble(KEY_LAST_LOCATION_LATITUDE,lastLocation.latitude)
                    putDouble(KEY_LAST_LOCATION_LONGITUDE,lastLocation.longitude)
                    fragment.arguments = this
                } } }

        showFragment(fragment)

    }

    private fun setupBottomNavigationListeners() {

        activityMainScreenBinding?.bottomNavigation?.apply {
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.commutersFragment -> {
                        if (currentFragment() !is CommuterFragment) {
                            showFragment(CommuterFragment())
                        }
                    }
                    R.id.settingsFragment -> {
                        if (currentFragment() !is SettingsFragment) {
                            showFragment(SettingsFragment())
                        }
                    }
                    R.id.statisticsFragment -> {
                        if (currentFragment() !is StatisticsFragment) {
                            showFragment(StatisticsFragment())
                        }
                    }
                    R.id.weatherFragment -> {

                        if (currentFragment() !is WeatherFragment) {
                            showFragment(WeatherFragment())
                        }
                    }
                }
                true
            }
        }
    }



    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToCommuterFragment(intent)

    }

    private fun navigateToCommuterFragment(intent: Intent?){
        if(intent?.action == ACTION_SHOW_COMMUTER_FRAGMENT && !isCommuterFragmentAtForeground){
            isCommuterFragmentAtForeground = true
            navigationController.navigate(R.id.action_global_commuterFragment)
        }
    }



    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.apply {
            this.beginTransaction().apply {
                findFragmentById(R.id.fragmentContainer)?.let(::detach)
                if (findFragmentByTag(fragment.javaClass.name) == null) {
                    add(R.id.fragmentContainer, fragment, fragment.javaClass.name)
                    addToBackStack(fragment.javaClass.name)
                } else {
                 attach(findFragmentByTag(fragment.javaClass.name)!!)
                }
                commit()
            }
            }
        }


    private fun currentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.fragmentContainer)
    }
    override fun onBackPressed() {
        if(currentFragment() is CommuterFragment){
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
        showFragment(CommuterFragment())
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