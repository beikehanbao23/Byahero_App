package com.example.commutingapp.views.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.commutingapp.R
import com.example.commutingapp.data.firebase.auth.FirebaseAuthenticatorWrapper
import com.example.commutingapp.data.firebase.auth.UserAuthenticationProcessor
import com.example.commutingapp.data.firebase.usr.FirebaseUserWrapper
import com.example.commutingapp.data.firebase.usr.UserDataProcessor
import com.example.commutingapp.data.firebase.usr.UserEmailProcessor
import com.example.commutingapp.data.others.Constants.ACTION_SHOW_COMMUTER_FRAGMENT
import com.example.commutingapp.databinding.ActivityMainScreenBinding
import com.example.commutingapp.utils.ui_utilities.ActivitySwitcher
import com.example.commutingapp.views.MenuButtons.CustomBackButton
import com.example.commutingapp.views.ui.fragments.CommuterFragment
import com.example.commutingapp.views.ui.fragments.SettingsFragment
import com.example.commutingapp.views.ui.fragments.StatisticsFragment
import com.example.commutingapp.views.ui.fragments.WeatherFragment
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserInfo
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeAttributes()

        val navigationToolbar = activityMainScreenBinding?.toolbar
        val navigationHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navigationController = navigationHostFragment.navController

        navigateToCommuterFragment(intent)

        setSupportActionBar(navigationToolbar)
        NavigationUI.setupWithNavController(navigationToolbar as Toolbar, navigationController)
        setupBottomNavigationListeners()

    }
    private fun setupBottomNavigationListeners(){

        activityMainScreenBinding?.bottomNavigation?.setOnItemSelectedListener {
            when(it.itemId){
                R.id.commutersFragment->setCurrentFragment(CommuterFragment())
                R.id.settingsFragment->setCurrentFragment(SettingsFragment())
                R.id.statisticsFragment->setCurrentFragment(StatisticsFragment())
                R.id.weatherFragment->setCurrentFragment(WeatherFragment())
            }
            true
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToCommuterFragment(intent)

    }

    private fun navigateToCommuterFragment(intent: Intent?){
        if(intent?.action == ACTION_SHOW_COMMUTER_FRAGMENT){
            navigationController.navigate(R.id.action_global_commuterFragment)
        }
    }



    private fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer,fragment)
            addToBackStack(null)
            commit()
        }
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

        ActivitySwitcher.startActivityOf(this, this, SignIn::class.java)
    }

    override fun onBackPressed() {
        CustomBackButton(this, this).applyDoubleClickToExit()
    }




}