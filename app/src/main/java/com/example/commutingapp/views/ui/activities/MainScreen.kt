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
import com.example.commutingapp.utils.others.Constants.ACTION_SHOW_COMMUTER_FRAGMENT
import com.example.commutingapp.databinding.ActivityMainScreenBinding
import com.example.commutingapp.utils.ui_utilities.ActivitySwitch
import com.example.commutingapp.views.ui.fragments.CommuterFragment
import com.example.commutingapp.views.ui.fragments.SettingsFragment
import com.example.commutingapp.views.ui.fragments.StatisticsFragment
import com.example.commutingapp.views.ui.fragments.WeatherFragment
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserInfo
import com.mapbox.mapboxsdk.Mapbox
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class MainScreen : AppCompatActivity() {
    private val firebaseUser = FirebaseUserWrapper()
    private val userData: UserDataProcessor<List<UserInfo>?> = UserDataProcessor(firebaseUser)
    private val userEmail: UserEmailProcessor<Task<Void>?> = UserEmailProcessor(firebaseUser)
    private val userAuthentication: UserAuthenticationProcessor<Task<AuthResult>> =
        UserAuthenticationProcessor(FirebaseAuthenticatorWrapper())

    private var activityMainScreenBinding: ActivityMainScreenBinding? = null
    private lateinit var navigationController: NavController
    private var isCommuterFragmentAtForeground = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeAttributes()
        navigateToCommuterFragment(intent)
        val navigationHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navigationController = navigationHostFragment.navController

        setSupportActionBar( activityMainScreenBinding?.toolbar)
        activityMainScreenBinding?.bottomNavigation?.setupWithNavController(navigationController)
        setupBottomNavigationListeners()



    }

    private fun setupBottomNavigationListeners(){

        activityMainScreenBinding?.bottomNavigation?.apply {

            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.commutersFragment -> replaceFragment(CommuterFragment())
                    R.id.settingsFragment -> replaceFragment(SettingsFragment())
                    R.id.statisticsFragment -> replaceFragment(StatisticsFragment())
                    R.id.weatherFragment -> replaceFragment(WeatherFragment())
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



    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.apply {

            if (!fragment.isAdded) {
                beginTransaction().apply {
                        if (findFragmentByTag(fragment.javaClass.name) == null) {
                            replace(R.id.fragmentContainer, fragment, fragment.javaClass.name)
                            addToBackStack(fragment.javaClass.name)
                        } else {
                            findFragmentByTag(fragment.javaClass.name)?.let {
                                replace(R.id.fragmentContainer, it,fragment.javaClass.name)
                            }
                        }
                        commit()
                    }
                }

        }

    }

    override fun onBackPressed() {
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
        Mapbox.getInstance(this, getString(R.string.MapsToken))
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