package com.example.commutingapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commutingapp.utils.FirebaseUserManager.AuthenticationManager
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SignUpViewModel:ViewModel(){

    private var sendEmailVerificationOnSuccess = MutableLiveData<Boolean>()
    fun sendEmailOnSuccess():LiveData<Boolean> = sendEmailVerificationOnSuccess

    private var sendEmailVerificationOnFail = MutableLiveData<Boolean>()
    fun sendEmailOnFailed():LiveData<Boolean> = sendEmailVerificationOnFail

    private var noInternet = MutableLiveData<Boolean>()
    fun noInternetStatus():LiveData<Boolean> = noInternet

    private var exceptionErrorMessage = MutableLiveData<String?>()
    fun getExceptionMessage():LiveData<String?> = exceptionErrorMessage


    fun signUpAccount(email:String,password:String){

        viewModelScope.launch(Dispatchers.IO) {

            AuthenticationManager.getFirebaseAuthInstance()
                .createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    AuthenticationManager.getCreatedUserAccount()
                    sendEmailVerification()
                    return@addOnCompleteListener
                }
                task.exception?.let {
                   handleSignUpExceptions(it)
                }
            }
        }


    }


    private fun handleSignUpExceptions(exception:Exception){

            try {
                throw exception
            } catch (networkException: FirebaseNetworkException) {
                noInternet.value = true
            } catch (ex: Exception) {
                exceptionErrorMessage.value = ex.message

        }
    }

  private fun sendEmailVerification(){

        AuthenticationManager.getFirebaseUserInstance().sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendEmailVerificationOnSuccess.postValue(true)
                    return@addOnCompleteListener
                }
                sendEmailVerificationOnFail.postValue(true)
            }

    }






}