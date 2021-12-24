package com.example.commutingapp.data.api

data class Weather(
    var alerts: Alerts,
    var current: Current,
    var forecast: Forecast,
    var location: Location
)