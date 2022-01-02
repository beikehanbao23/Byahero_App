package com.example.commutingapp.data.model

data class Weather(
    var alerts: Alerts,
    var current: Current,
    var forecast: Forecast,
    var location: Location
)