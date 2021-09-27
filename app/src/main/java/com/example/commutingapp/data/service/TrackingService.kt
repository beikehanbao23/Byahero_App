package com.example.commutingapp.data.service

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import com.example.commutingapp.data.others.Constants.ACTION_PAUSE_SERVICE
import com.example.commutingapp.data.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.commutingapp.data.others.Constants.ACTION_STOP_SERVICE

class TrackingService :LifecycleService(){

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let{
            when(it.action){
                ACTION_START_OR_RESUME_SERVICE->{
                Log.e("Stats","Started")
                }
                ACTION_PAUSE_SERVICE->{
                    Log.e("Stats","Paused")
                }
                ACTION_STOP_SERVICE->{
                    Log.e("Stats","Stopped")
                }
                else -> {
                    Log.e("Stats","Crap")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}