package com.example.commutingapp.utils.others

import com.mapbox.mapboxsdk.geometry.LatLng

interface FragmentToActivity< T> {
    fun onFirstNotify()
    fun onSecondNotify()
    fun onThirdNotify(fragment:T,destination: LatLng?, lastKnownLocation:LatLng?)
}