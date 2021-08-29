package com.example.commutingapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commutingapp.R
import com.example.commutingapp.utils.FirebaseUserManager.FirebaseManager
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

            FirebaseManager.getFirebaseAuthInstance()
                .createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseManager.getCreatedUserAccount()
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
        FirebaseManager.getFirebaseUserInstance().sendEmailVerification().addOnCompleteListener { task->
            if(task.isSuccessful){
                emailVerification.value = Event(true)
                return@addOnCompleteListener
            }
            exceptionErrorMessage.value = R.string.sendingEmailErrorMessage.toString()
        }
    }






}