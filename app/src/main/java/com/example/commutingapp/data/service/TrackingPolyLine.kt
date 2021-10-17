package com.example.commutingapp.data.service

import android.location.Location
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.mapbox.mapboxsdk.geometry.LatLng

class TrackingPolyLine :LifecycleService(){
    override fun onCreate() {
        super.onCreate()
        liveDataOuterPolyLine.postValue(mutableListOf())
    }
    fun polyLine():MutableLiveData<outerPolyline> = liveDataOuterPolyLine
    companion object{
     val liveDataOuterPolyLine = MutableLiveData<outerPolyline>()
}
     fun addEmptyPolyLines() = liveDataOuterPolyLine.value?.apply {
        add(mutableListOf())
        liveDataOuterPolyLine.postValue(this)
    }?: liveDataOuterPolyLine.postValue(mutableListOf(mutableListOf()))

     fun addPolyline(location: Location?){
        location?.let {
            val position = LatLng(location.latitude,location.longitude)
                liveDataOuterPolyLine.value?.apply {
                last().add(position)
                 liveDataOuterPolyLine.postValue(this)
            }
        }
    }


}