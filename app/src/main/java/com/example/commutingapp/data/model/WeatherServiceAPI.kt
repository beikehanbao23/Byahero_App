package com.example.commutingapp.data.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface WeatherServiceAPI {

@GET("forecast.json")
fun getWeatherData(@QueryMap map:HashMap<String, String>): Call<Weather>


}