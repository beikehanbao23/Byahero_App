package com.example.commutingapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commutingapp.utils.FirebaseUserManager.AuthenticationManager
import com.example.commutingapp.utils.ui_utilities.Event
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IntroSliderViewModel : ViewModel() {


    var onNavigateToDetailsSuccess = MutableLiveData<Event<Boolean>>()
        private set


    fun setUserSignInProvider() {
        viewModelScope.launch(Dispatchers.Main) {
            if (AuthenticationManager.hasAccountRemainingInCache()) {
                if (signInSuccessWithAnyProviders()) {
                    onNavigateToDetailsSuccess.value = Event(true)
                }
            }
        }
    }

    private suspend fun signInSuccessWithAnyProviders(): Boolean {
        return withContext(Dispatchers.IO) {
                AuthenticationManager.getFirebaseUserInstance().isEmailVerified ||
                        isUserSignInUsingFacebook() ||
                        isUserSignInUsingGoogle()

        }


    }

    private fun isUserSignInUsingFacebook() = getProviderIdResult(FacebookAuthProvider.PROVIDER_ID)
    private fun isUserSignInUsingGoogle() = getProviderIdResult(GoogleAuthProvider.PROVIDER_ID)


    private  fun getProviderIdResult(id: String): Boolean {
            AuthenticationManager.getFirebaseUserInstance().providerData.forEach {
                Log.e("Result", it.providerId)
                if (it.providerId == id) {
                    return true // return ui.getProviderId().equals(id) does not work here, always returning 'firebase' as providerId
                }
        }
        return false
    }


}