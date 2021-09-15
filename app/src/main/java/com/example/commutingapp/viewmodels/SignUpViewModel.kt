package com.example.commutingapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commutingapp.data.Auth.FirebaseAuthenticatorWrapper
import com.example.commutingapp.data.Auth.UserAuthenticationProcessor
import com.example.commutingapp.data.Usr.FirebaseUserWrapper
import com.example.commutingapp.data.Usr.UserDataProcessor
import com.example.commutingapp.data.Usr.UserEmailProcessor
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SignUpViewModel : ViewModel() {

    private var sendEmailVerificationOnSuccess = MutableLiveData<Boolean>()
    fun sendEmailOnSuccess(): LiveData<Boolean> = sendEmailVerificationOnSuccess

    private var sendEmailVerificationOnFail = MutableLiveData<Boolean>()
    fun sendEmailOnFailed(): LiveData<Boolean> = sendEmailVerificationOnFail

    private var noInternet = MutableLiveData<Boolean>()
    fun noInternetStatus(): LiveData<Boolean> = noInternet

    private var exceptionErrorMessage = MutableLiveData<String?>()
    fun getExceptionMessage(): LiveData<String?> = exceptionErrorMessage

    private val userAuthentication = FirebaseAuthenticatorWrapper()
    private val authenticate: UserAuthenticationProcessor<Task<AuthResult>> = UserAuthenticationProcessor(userAuthentication)

    private val user = FirebaseUserWrapper()
    private val userDataProcessor: UserDataProcessor<List<UserInfo>?> = UserDataProcessor(user)
    private val userEmailProcessor: UserEmailProcessor<Task<Void>?> = UserEmailProcessor(user)











    fun signUpAccount(email: String, password: String) {

        viewModelScope.launch(Dispatchers.IO) {

            authenticate.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        userDataProcessor.saveCreatedAccount()
                        sendEmailVerification()
                        return@addOnCompleteListener
                    }
                    task.exception?.let {
                        handleSignUpExceptions(it)
                    }
                }
        }


    }


    private fun handleSignUpExceptions(exception: Exception) {

        try {
            throw exception
        } catch (networkException: FirebaseNetworkException) {
            noInternet.value = true
        } catch (ex: Exception) {
            exceptionErrorMessage.value = ex.message

        }
    }

    private fun sendEmailVerification() {

        userEmailProcessor.sendEmailVerification()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendEmailVerificationOnSuccess.postValue(true)
                    return@addOnCompleteListener
                }
                sendEmailVerificationOnFail.postValue(true)
            }

    }


}