package com.example.commutingapp.viewmodels

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commutingapp.data.firebase.usr.FirebaseUserWrapper
import com.example.commutingapp.data.firebase.usr.UserEmailProcessor
import com.example.commutingapp.utils.ui_utilities.Event
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.*

private const val timerCounts: Long = 120000
private const val oneSecond: Long = 1000
private const val coroutineInterval: Long = 2200

class EmailSentViewModel : ViewModel() {

    private lateinit var verificationTimer: CountDownTimer

    lateinit var coroutineIOJob: Job
    private set

    private var timerOnRunningSecondsValue = MutableLiveData<Int>()
    fun getTimerOnRunningStatus():LiveData<Int> = timerOnRunningSecondsValue

    private var timerOnFinished = MutableLiveData<Boolean>()
    fun getTimerOnFinishedStatus():LiveData<Boolean> = timerOnFinished

    private var mainScreenActivityTransition = MutableLiveData<Event<Boolean>>()
    fun getMainScreenTransitionStatus():LiveData<Event<Boolean>> = mainScreenActivityTransition

    private var noInternetActivityTransition = MutableLiveData<Boolean>()
    fun getInternetConnectionStatus():LiveData<Boolean> = noInternetActivityTransition

    private var sendEmailOnSuccess = MutableLiveData<Boolean>()
    fun sendEmailOnSuccess():LiveData<Boolean> = sendEmailOnSuccess

    private var sendEmailOnFailed = MutableLiveData<Boolean>()
    fun sendEmailOnFail():LiveData<Boolean> = sendEmailOnFailed

    private var displayUserEmail = MutableLiveData<String>()

    private var userEmailProcessor:UserEmailProcessor<Task<Void>?> = UserEmailProcessor(FirebaseUserWrapper())

    fun displayUserEmailToTextView(): LiveData<String> {

        viewModelScope.launch(Dispatchers.Main) {
            userEmailProcessor.getUserEmail()?.let {
                displayUserEmail.value = it
            }
        }.also {
            return displayUserEmail
        }

    }

    fun refreshEmailSynchronously() {
        coroutineIOJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                reloadUserEmail()
                Log.d("COROUTINE STATUS: ", "$isActive")
                delay(coroutineInterval)
            }
        }
    }

    fun sendEmailVerification() {
        viewModelScope.launch(Dispatchers.IO) {
            userEmailProcessor.sendEmailVerification()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sendEmailOnSuccess.postValue(true)
                        return@addOnCompleteListener
                    }
                    sendEmailOnFailed.postValue(true)
                }
        }
    }

    private suspend fun reloadUserEmail() {
        coroutineScope {
            userEmailProcessor.reloadEmail()?.addOnCompleteListener { reload ->
                if (reload.isSuccessful && userEmailProcessor.isEmailVerified() == true) {
                    mainScreenActivityTransition.postValue(Event(true))
                    return@addOnCompleteListener
                }
                reload.exception?.let {
                     handleEmailVerificationExceptions(it)
                }
            }
            }
        }

     private fun handleEmailVerificationExceptions(exception:Exception) {
            try {
                throw exception
            } catch (networkException: FirebaseNetworkException) {
                noInternetActivityTransition.value = true
            } finally {
                coroutineIOJob.cancel()
            }



    }

    fun startTimer() {
        viewModelScope.launch(Dispatchers.Main) {
            verificationTimer = object : CountDownTimer(timerCounts, oneSecond) {
                override fun onTick(millisUntilFinished: Long) {
                    val timeLeft = millisUntilFinished / oneSecond
                    timerOnRunningSecondsValue.value = timeLeft.toInt()
                }

                override fun onFinish() {
                    timerOnFinished.value = true
                    stopTimer()
                }
            }.start()
        }
    }

    private fun stopTimer() {
        if (::verificationTimer.isInitialized) {
            verificationTimer.cancel()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
