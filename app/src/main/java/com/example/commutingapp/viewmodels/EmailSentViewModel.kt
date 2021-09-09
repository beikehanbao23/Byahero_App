package com.example.commutingapp.viewmodels

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commutingapp.utils.FirebaseUserManager.FirebaseManager
import com.example.commutingapp.utils.ui_utilities.Event
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.*

private const val timerCounts: Long = 15000
private const val oneSecond: Long = 1000
private const val coroutineInterval: Long = 2200

class EmailSentViewModel : ViewModel() {

    private lateinit var verificationTimer: CountDownTimer

    lateinit var coroutine_IO_Job: Job
    private set

    private var timerOnRunningSecondsValue = MutableLiveData<Int>()
    private var timerOnFinished = MutableLiveData<Boolean>()
    private var mainScreenActivityTransition = MutableLiveData<Event<Boolean>>()
    private var noInternetActivityTransition = MutableLiveData<Boolean>()
    private var sendEmailOnSuccess = MutableLiveData<Boolean>()
    private var sendEmailOnFailed = MutableLiveData<Boolean>()

    fun getTimerOnRunningStatus():LiveData<Int> = timerOnRunningSecondsValue
    fun getTimerOnFinishedStatus():LiveData<Boolean> = timerOnFinished
    fun getMainScreenTransitionStatus():LiveData<Event<Boolean>> = mainScreenActivityTransition
    fun getInternetConnectionStatus():LiveData<Boolean> = noInternetActivityTransition
    fun getEmailOnSuccessStatus():LiveData<Boolean> = sendEmailOnSuccess
    fun getEmailOnFailureStatus():LiveData<Boolean> = sendEmailOnFailed

    private var displayUserEmail = MutableLiveData<String>()


    fun displayUserEmailToTextView(): LiveData<String> {

        viewModelScope.launch(Dispatchers.Main) {
            FirebaseManager.getFirebaseUserInstance().email?.let {
                displayUserEmail.value = it
            }
        }.also {
            return displayUserEmail
        }

    }

    fun refreshEmailSynchronously() {
        coroutine_IO_Job = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                reloadUserEmail()
                Log.d("COROUTINE STATUS: ", "$isActive")
                delay(coroutineInterval)
            }
        }
    }

    fun sendEmail() {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseManager.getFirebaseUserInstance().sendEmailVerification()
                .addOnCompleteListener { task ->
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
            FirebaseManager.getFirebaseUserInstance().reload().addOnCompleteListener { reload ->
                if (reload.isSuccessful && FirebaseManager.getFirebaseUserInstance().isEmailVerified) {
                    mainScreenActivityTransition.postValue(Event(true))
                    return@addOnCompleteListener
                }
                reload.exception?.let {
                    runBlocking { handleEmailVerificationExceptions(reload) }
                }
            }
            }
        }

    private fun handleEmailVerificationExceptions(task: Task<*>) {
            try {
                throw task.exception!!
            } catch (networkException: FirebaseNetworkException) {
                noInternetActivityTransition.value = true
            } catch (ex: Exception) {
                Log.e("Exception", ex.message.toString())
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
