package com.example.commutingapp.data.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class WeatherDto(
    @SerializedName("alerts")
    val alerts: Alerts,
    @SerializedName("current")
    val current: Current,
    @SerializedName("forecast")
    val forecast: Forecast,
    @SerializedName("location")
    val location: Location
)