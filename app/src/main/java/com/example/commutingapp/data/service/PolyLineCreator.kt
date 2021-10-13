package com.example.commutingapp.data.service

import android.location.Location
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mapbox.mapboxsdk.geometry.LatLng

class PolyLineCreator :LifecycleService(){
    override fun onCreate() {
        super.onCreate()
        liveDataOuterPolyline.postValue(mutableListOf())
    }
    fun polyLine():MutableLiveData<outerPolyline> = liveDataOuterPolyline
    companion object{
     val liveDataOuterPolyline = MutableLiveData<outerPolyline>()
}
     fun addEmptyPolyLines() = liveDataOuterPolyline.value?.apply {
        add(mutableListOf())
        liveDataOuterPolyline.postValue(this)
    }?: liveDataOuterPolyline.postValue(mutableListOf(mutableListOf()))

     fun addPolyline(location: Location?){
        location?.let {
            val position = LatLng(location.latitude,location.longitude)
                liveDataOuterPolyline.value?.apply {
                last().add(position)
                 liveDataOuterPolyline.postValue(this)
            }
        }
    }


}