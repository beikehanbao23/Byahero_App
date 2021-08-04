package com.example.commutingapp

import FirebaseUserManager.FirebaseUserManager
import Logger.CustomToastMessage
import MenuButtons.BackButtonDoubleClicked
import MenuButtons.CustomBackButton
import MenuButtons.backButton
import Screen.ScreenDimension
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.LoginManager
import java.util.*

class MainScreen : AppCompatActivity(), BackButtonDoubleClicked {
    private var toastMessageBackButton: CustomToastMessage? = null
    private lateinit var nameTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenDimension(window).windowToFullScreen()
        setContentView(R.layout.activity_main_screen)
        toastMessageBackButton =
            CustomToastMessage(this, getString(R.string.doubleTappedMessage), 10)
        nameTextView = findViewById(R.id.nameTextView)
        FirebaseUserManager.initializeFirebase()
        displayName()
    }







    //todo fix later
    private val name: String?
        get() {
            for (userInfo in FirebaseUserManager.getFirebaseUserInstance().providerData) {
                if (userInfo.providerId == "facebook.com" || userInfo.providerId == "google.com") {
                    return FirebaseUserManager.getFirebaseUserInstance().displayName
                }
            }
            return getFilteredEmail(FirebaseUserManager.getFirebaseUserInstance().email)
        }

    private fun displayName() {
        nameTextView.text = name
    }

    private fun getFilteredEmail(userEmail: String?): String? {
        val emailLists = emailExtensions
        if (userEmail != null) {
            for (counter in emailLists.indices) {
                val emailExtension = emailLists[counter]
                if (userEmail.contains(emailExtension)) {
                    return userEmail.replace(emailExtension.toRegex(), "")
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
/*
    private fun checkFacebookTokenIfExpired() {
        object : AccessTokenTracker() {
            override fun onCurrentAccessTokenChanged(
                oldAccessToken: AccessToken,
                currentAccessToken: AccessToken
            ) {
                if (currentAccessToken == null) {
                    signOutAccount();
                    showExpiredTokenDialog()
                    Log.e(javaClass.name, "Token is Expired")
                }
            }
        }
    }

 */
    fun LogoutButtonClicked(view: View?) {
        Log.e(javaClass.name, "Logging out!")
        putUserToLoginFlow()
    }

    private fun signOutAccount() {
        Log.e("Logout status:","SUCCESS");
        LoginManager.getInstance().logOut()
        FirebaseUserManager.getFirebaseAuthInstance().signOut()
    }

    private fun putUserToLoginFlow() {
        signOutAccount()
        showSignInForm()
    }

    private fun showSignInForm() {
        startActivity(Intent(this, SignIn::class.java))
        finish()
    }

    private fun showExpiredTokenDialog() {
        startActivity(Intent(this, TokenExpired::class.java))
        finish()
    }

    override fun onBackPressed() {
        backButtonClicked()
    }

    override fun backButtonClicked() {
        CustomBackButton(label@ BackButtonDoubleClicked {
            if (backButton.isDoubleTapped()) {
                toastMessageBackButton!!.hideToast()
                super.onBackPressed()
                return@BackButtonDoubleClicked
            }
            toastMessageBackButton!!.showToast()
            backButton.registerFirstClick()
        }).backButtonIsClicked()
    }
}