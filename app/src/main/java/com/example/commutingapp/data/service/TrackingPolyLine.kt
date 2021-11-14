package com.example.commutingapp.data.service

import android.app.Activity
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.example.commutingapp.utils.others.Constants
import com.mapbox.mapboxsdk.annotations.PolylineOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import timber.log.Timber

typealias alias_innerPolyline = MutableList<LatLng>
typealias alias_outerPolyline = MutableList<alias_innerPolyline>

class TrackingPolyLine {

    fun polyLine():MutableLiveData<alias_outerPolyline> = liveDataOuterPolyLine

    companion object{
    private val liveDataOuterPolyLine = MutableLiveData<alias_outerPolyline>()
        var outerPolyline = mutableListOf<alias_innerPolyline>()
    }

    init{
        liveDataOuterPolyLine.postValue(mutableListOf())
    }
     fun addEmptyPolyLines() = liveDataOuterPolyLine.value?.apply {
        add(mutableListOf())
        liveDataOuterPolyLine.postValue(this)
    }?: liveDataOuterPolyLine.postValue(mutableListOf(mutableListOf()))

     fun addPolyline(location: Location?){
        location?.let {
            val userLocation = LatLng(location.latitude,location.longitude)
                liveDataOuterPolyLine.value?.apply {
                try{ last().add(userLocation)}catch (e:NoSuchElementException){Timber.d(e)}
                 liveDataOuterPolyLine.postValue(this)
            }
        }
    }
     fun getAllPolyLines():PolylineOptions? {
        outerPolyline.forEach {
            return customPolylineAppearance().addAll(it)
        }
        return null
    }

     fun getLatestPolyLine():PolylineOptions? {

        if (hasExistingInnerPolyLines()) {
            val innerPolylinePosition = outerPolyline.last().size - 2
            val preLastLatLng = outerPolyline.last()[innerPolylinePosition]
            val lastLatLng = outerPolyline.last().last()

            customPolylineAppearance()
                .add(preLastLatLng)
                .add(lastLatLng).apply {
                    return this
                } }

        return null
    }


    fun getPolyLineLastLocation():LatLng? {
        return try {
            outerPolyline.last().last()
        }catch (e:NoSuchElementException){
            Timber.d(e)
            null
        }
    }

     fun hasExistingInnerAndOuterPolyLines() =
        outerPolyline.isNotEmpty() && outerPolyline.last().isNotEmpty()

     private fun hasExistingInnerPolyLines() =
        outerPolyline.isNotEmpty() && outerPolyline.last().size > 1

    private fun customPolylineAppearance(): PolylineOptions {

        return PolylineOptions()
            .color(Constants.POLYLINE_COLOR)
            .width(Constants.POLYLINE_WIDTH)


    }
}