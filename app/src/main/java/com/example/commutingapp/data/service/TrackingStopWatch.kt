package com.example.commutingapp.data.service

import androidx.lifecycle.MutableLiveData
import com.example.commutingapp.data.service.TrackingService.Companion.is_Tracking
import com.example.commutingapp.utils.others.Constants.ONE_SECOND
import com.example.commutingapp.utils.others.Constants.STOPWATCH_INTERVAL
import com.example.commutingapp.utils.others.Watch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackingStopWatch :  Watch {
    private val timeRunInSeconds = MutableLiveData<Long>()


    companion object{
         val timeRunInMillis = MutableLiveData<Long>()
    }
    fun getTimeRunMillis():MutableLiveData<Long> = timeRunInMillis
    fun getTimeCommuteInSeconds():MutableLiveData<Long> = timeRunInSeconds

     fun postInitialValues(){
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }
    private var isTimerEnabled = false
    private var timeCommute:Long = 0L
    private var lapTime:Long = 0L
    private var timeStarted:Long = 0L
    private var lastSecondTimestamp:Long = 0L


    override fun pause() {
        isTimerEnabled = false
    }

    override fun stop() {

    }

    override fun start() {
        initializeTimer()
        CoroutineScope(Dispatchers.Main).launch{
            runTimer()
        }
    }
    private suspend fun runTimer(){

        while(is_Tracking.value!!) {
            createLapTime()
            increaseMillis()
            if (secondHasElapsed()) {
                increaseSeconds()
                increaseTimeStamp()
            }
            delay(STOPWATCH_INTERVAL)
        }
        increaseTimeCommute()
    }
    private fun initializeTimer(){
        isTimerEnabled = true
        timeStarted = System.currentTimeMillis()
    }
    private fun increaseTimeCommute(){
        timeCommute += lapTime
    }
    private fun createLapTime(){
        lapTime = System.currentTimeMillis() - timeStarted
    }
    private fun increaseMillis(){
        timeRunInMillis.postValue(timeCommute + lapTime)
    }
    private fun increaseSeconds(){
        timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
    }
    private fun increaseTimeStamp(){
        lastSecondTimestamp += ONE_SECOND
    }
    private fun secondHasElapsed():Boolean{ return timeRunInMillis.value!! >= lastSecondTimestamp + ONE_SECOND}
}