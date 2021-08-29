package com.example.commutingapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.commutingapp.utils.FirebaseUserManager.FirebaseManager
import com.example.commutingapp.utils.ui_utilities.Event
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider

class SplashScreenViewModel : ViewModel() {


     var onNavigateToDetailsSuccess = MutableLiveData<Event<Boolean>>()
     private set


    fun setUserSignInProvider(){

        if(FirebaseManager.hasAccountRemainingInCache()) {
            if (signInSuccessWithAnyProviders()) {
                onNavigateToDetailsSuccess.value = Event(true)
                return;
            }
        }
    }

    private fun signInSuccessWithAnyProviders(): Boolean {
        return FirebaseManager.getFirebaseUserInstance().isEmailVerified ||
                isUserSignInUsingFacebook() ||
                isUserSignInUsingGoogle()


    }

    private fun isUserSignInUsingFacebook() = getProviderIdResult(FacebookAuthProvider.PROVIDER_ID)
    private fun isUserSignInUsingGoogle() = getProviderIdResult(GoogleAuthProvider.PROVIDER_ID)


    private fun getProviderIdResult(id: String): Boolean {
        FirebaseManager.getFirebaseUserInstance().providerData.forEach {
            Log.e("Result", it.providerId)
            if (it.providerId == id) {
                return true // return ui.getProviderId().equals(id) does not work here, always returning 'firebase' as providerId
            }
        }
        return false
    }



}