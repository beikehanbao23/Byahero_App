package com.example.commutingapp.viewmodels

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.commutingapp.utils.FirebaseUserManager.FirebaseManager
import com.example.commutingapp.utils.ui_utilities.Event
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException

private const val twoMinutes: Long = 120000
private const val oneSecond: Long = 1000

class EmailSentViewModel:ViewModel() {

    private lateinit var verificationTimer: CountDownTimer
    private val _seconds = MutableLiveData<Int>()
    private val timer = MutableLiveData<Event<Boolean>>()
    private var displayUserEmail = MutableLiveData<String>()
    private val emailReload = MutableLiveData<Boolean>()

    fun timerOnFinished() : LiveData<Event<Boolean>> = timer
    fun timerOnRunning():LiveData<Int> = _seconds

    fun displayUserEmailToTextView():LiveData<String>{
        FirebaseManager.getFirebaseUserInstance().email?.let {
            displayUserEmail.value = it
        }.also{
            return displayUserEmail
        }

    }

    fun emailRefresh(){
        FirebaseManager.getFirebaseUserInstance().reload().addOnCompleteListener(){
            if(it.isSuccessful){
                emailReload.postValue(true)
            }
        }
    }

    fun reloadUserEmail(){
        FirebaseManager.getFirebaseUserInstance().reload().addOnCompleteListener(){task->
        if(task.isSuccessful){

        }
        }
    }

    private fun handleEmailVerificationException(task: Task<*>) {
        try {
            throw task.exception!!
        } catch (firebaseNetworkException: FirebaseNetworkException) {
        //    exitThread = true
           // showNoInternetActivity()
        } catch (ignored: Exception) {
        }
    }


    fun startTimer(){

        verificationTimer = object : CountDownTimer(twoMinutes, oneSecond){
            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished/oneSecond
               _seconds.postValue(timeLeft.toInt())
            }
            override fun onFinish() {
                timer.postValue(Event(true))
                stopTimer()
            }
        }.start()
    }

    private fun stopTimer(){

        if(::verificationTimer.isInitialized){
            verificationTimer.cancel()
        }

    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
/*

3. threading for email
 */