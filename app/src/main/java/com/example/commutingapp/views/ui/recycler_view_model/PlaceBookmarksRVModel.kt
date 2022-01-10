package com.example.commutingapp.views.ui.recycler_view_model

import com.mapbox.mapboxsdk.geometry.LatLng

data class PlaceBookmarksRVModel(
    val placeName:String,
    val location:LatLng,
    val placeText:String
)