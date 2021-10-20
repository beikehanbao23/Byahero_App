package com.example.commutingapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.commutingapp.data.firebase.usr.FirebaseUserWrapper
import com.example.commutingapp.data.firebase.usr.UserDataProcessor
import com.example.commutingapp.data.firebase.usr.UserEmailProcessor
import com.example.commutingapp.utils.others.Event
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class IntroSliderViewModel : ViewModel() {

    private var onNavigateToDetailsSuccess = MutableLiveData<Event<Boolean>>()
    private val firebaseUser = FirebaseUserWrapper()
    private val userData:UserDataProcessor<List< UserInfo>?> = UserDataProcessor(firebaseUser)
    private val userEmail:UserEmailProcessor<Task<Void>?> = UserEmailProcessor(firebaseUser)

    fun navigateToDetailsOnSuccess(): LiveData<Event<Boolean>> = onNavigateToDetailsSuccess


    fun setUserSignInProvider() = runBlocking {

        if (userData.hasAccountRemainingInCache()) {
            if (signInSuccessWithAnyProvidersAsync()) {
                onNavigateToDetailsSuccess.value = Event(true)

            }
        }
    }


    private suspend fun signInSuccessWithAnyProvidersAsync(): Boolean {
        return withContext(Dispatchers.IO) {
            userEmail.isEmailVerified() == true ||
                    isUserSignInUsingFacebook() ||
                    isUserSignInUsingGoogle()
        }

    }

    private fun isUserSignInUsingFacebook() = getProviderIdResult(FacebookAuthProvider.PROVIDER_ID)
    private fun isUserSignInUsingGoogle() = getProviderIdResult(GoogleAuthProvider.PROVIDER_ID)


    private fun getProviderIdResult(id: String): Boolean {

    userData.getUserProviderData()?.forEach {
            if (it.providerId == id) {
                return true
            }
        }
        return false
    }
}

