package com.example.commutingapp.views.ui.recycler_view_model

data class WeatherRVModel(
    val time:String,
    val icon:String? = null,
    val temperature:String,
    val windSpeed:String
)