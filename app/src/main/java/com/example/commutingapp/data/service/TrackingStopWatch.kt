package com.example.commutingapp.data.service

import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.commutingapp.data.others.Constants.STOPWATCH_INTERVAL
import com.example.commutingapp.data.others.Watch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackingStopWatch : LifecycleService(), Watch {
    private val timeRunInSeconds = MutableLiveData<Long>()
    private val tracking = MutableLiveData<Boolean>()
    companion object{
        val timeRunInMillis = MutableLiveData<Long>()
    }

    override fun onCreate() {
        super.onCreate()
        TrackingService.is_Tracking.observe(this){
            tracking.value = it
        }

    }

    private var isTimerEnabled = false
    private var timeCommute:Long = 0L
    private var lapTime:Long = 0L
    private var timeStarted:Long = 0L
    private var lastSecondTimestamp:Long = 0L


    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun start() {
            isTimerEnabled = true
            timeStarted = System.currentTimeMillis()

        CoroutineScope(Dispatchers.Main).launch{
        while(tracking.value!!) {
            lapTime = System.currentTimeMillis() - timeStarted
            timeRunInSeconds.postValue(timeCommute + lapTime)
            if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                timeRunInMillis.postValue(timeRunInSeconds.value!! + 1)
                lastSecondTimestamp += 1000L
            }
            delay(STOPWATCH_INTERVAL)
            timeCommute += lapTime
        }
        }
    }


}