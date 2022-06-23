package com.example.commutingapp.data.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Alerts(
    @SerializedName("alert")
    val alert: List<Any>
)