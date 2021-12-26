package com.example.commutingapp.views.ui.recycler_view

data class WeatherRecyclerViewModel(
    val time:String,
    val icon:String? = null,
    val temperature:String,
    val windSpeed:String
)