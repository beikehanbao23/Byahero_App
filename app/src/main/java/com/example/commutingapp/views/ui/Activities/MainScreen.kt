package com.example.commutingapp.views.ui.Activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.commutingapp.R
import com.example.commutingapp.data.firebase.Auth.FirebaseAuthenticatorWrapper
import com.example.commutingapp.data.firebase.Auth.UserAuthenticationProcessor
import com.example.commutingapp.data.firebase.Usr.FirebaseUserWrapper
import com.example.commutingapp.data.firebase.Usr.UserDataProcessor
import com.example.commutingapp.data.firebase.Usr.UserEmailProcessor
import com.example.commutingapp.databinding.ActivityMainScreenBinding
import com.example.commutingapp.utils.ui_utilities.ActivitySwitcher
import com.example.commutingapp.views.MenuButtons.CustomBackButton
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserInfo
import java.util.*

class MainScreen : AppCompatActivity() {
    private val firebaseUser = FirebaseUserWrapper()
    private val userData: UserDataProcessor<List<UserInfo>?> = UserDataProcessor(firebaseUser)
    private val userEmail: UserEmailProcessor<Task<Void>?> = UserEmailProcessor(firebaseUser)
    private val userAuthentication: UserAuthenticationProcessor<Task<AuthResult>> =
        UserAuthenticationProcessor(FirebaseAuthenticatorWrapper())

    private var activityMainScreenBinding: ActivityMainScreenBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            initializeAttributes()
            val navigationToolbar = activityMainScreenBinding?.toolbar
            val navigationHostFragment = supportFragmentManager.findFragmentById(R.id.FragmentContainer) as NavHostFragment
            val navigationController = navigationHostFragment.navController

            setSupportActionBar(navigationToolbar)
            NavigationUI.setupWithNavController(navigationToolbar as Toolbar, navigationController)

            navigationController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.settingsFragment, R.id.statisticsFragment, R.id.commuterFragment -> {
                        activityMainScreenBinding?.bottomNavigation?.visibility = View.VISIBLE
                    }
                    else -> activityMainScreenBinding?.bottomNavigation?.visibility = View.INVISIBLE
                }
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


    //
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
        activityMainScreenBinding?.nameTextView?.text = userProfileName

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
        putToLoginFlow()
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