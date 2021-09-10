package com.example.commutingapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commutingapp.utils.FirebaseUserManager.AuthenticationManager
import com.example.commutingapp.utils.ui_utilities.Event
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SignUpViewModel:ViewModel(){

    var emailVerification = MutableLiveData<Event<Boolean>>()
    private set

    var noInternet = MutableLiveData<Event<Boolean>>()// should not be Event
    private set

    var exceptionErrorMessage = MutableLiveData<String?>()
    private set


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
                    handleSignUpExceptions(task)
                }
            }
        }


    }


   private fun handleSignUpExceptions(task: Task<*>){
        try {
            task.exception!!
        }catch (networkException:FirebaseNetworkException){
            noInternet.value = Event(true)
        }catch (ex:Exception){
            exceptionErrorMessage.value = ex.message
        }
    }

  private fun sendEmailVerification(){
      viewModelScope.launch(Dispatchers.IO){
        AuthenticationManager.getFirebaseUserInstance().sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                emailVerification.value = Event(true)
                return@addOnCompleteListener
            }

        }
        }
    }






}