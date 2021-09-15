package com.example.commutingapp.views.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.commutingapp.data.Auth.FirebaseAuthenticatorWrapper
import com.example.commutingapp.data.Auth.UserAuthenticationProcessor
import com.example.commutingapp.data.Usr.FirebaseUserWrapper
import com.example.commutingapp.data.Usr.UserDataProcessor
import com.example.commutingapp.data.Usr.UserEmailProcessor
import com.example.commutingapp.databinding.ActivityMainScreenBinding
import com.example.commutingapp.utils.ui_utilities.ActivitySwitcher
import com.example.commutingapp.utils.ui_utilities.ScreenDimension
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
        ScreenDimension(window).setWindowToFullScreen()
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