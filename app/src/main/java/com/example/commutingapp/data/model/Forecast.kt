package com.example.commutingapp.data.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Forecast(
    @SerializedName("forecastday")
    val forecastday: List<Forecastday>
)