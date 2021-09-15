package com.example.commutingapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.commutingapp.data.Auth.AuthenticationManager
import com.example.commutingapp.utils.ui_utilities.Event
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class IntroSliderViewModel : ViewModel() {

    private var onNavigateToDetailsSuccess = MutableLiveData<Event<Boolean>>()
    fun navigateToDetailsOnSuccess(): LiveData<Event<Boolean>> = onNavigateToDetailsSuccess




     fun setUserSignInProvider() = runBlocking {

        AuthenticationManager.initializeFirebaseApp()
        AuthenticationManager.getCreatedUserAccount()

        if (AuthenticationManager.hasAccountRemainingInCache()) {
            if (signInSuccessWithAnyProvidersAsync()) {
                onNavigateToDetailsSuccess.value = Event(true)

            }
        }
    }


     }




    private suspend fun signInSuccessWithAnyProvidersAsync(): Boolean {

        return withContext(Dispatchers.IO) {
            AuthenticationManager.getFirebaseUserInstance().isEmailVerified ||
                    isUserSignInUsingFacebook() ||
                    isUserSignInUsingGoogle()
        }

    }

    private fun isUserSignInUsingFacebook() = getProviderIdResult(FacebookAuthProvider.PROVIDER_ID)
    private fun isUserSignInUsingGoogle() = getProviderIdResult(GoogleAuthProvider.PROVIDER_ID)


    private fun getProviderIdResult(id: String): Boolean {
        AuthenticationManager.getFirebaseUserInstance().providerData.forEach {
            if (it.providerId == id) {
                return true
            }
        }
        return false
    }

