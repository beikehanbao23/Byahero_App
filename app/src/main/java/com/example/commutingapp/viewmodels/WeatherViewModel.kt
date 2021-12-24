package com.example.commutingapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.commutingapp.data.api.Weather
import com.example.commutingapp.data.api.WeatherService
import com.example.commutingapp.data.api.WeatherServiceAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class WeatherViewModel : ViewModel() {
    private var map: HashMap<String, String> = HashMap()
    private var request: WeatherServiceAPI
    private val location = MutableLiveData<String>()
    fun getLocation():LiveData<String> = location

    init {
        map.apply {
            this["key"] = "9b4e21f5ba214091bd7110616212312"
            this["q"] = "Manila"
            this["days"] = "1"
            this["aqi"] = "yes"
            this["alerts"] = "yes"
        }
        request = WeatherService.buildService(WeatherServiceAPI::class.java)
    }

    fun requestWeather(cityName: String) {
        map["q"] = cityName
        val call = request.getWeatherData(map)
        call.enqueue(weatherCallback())
    }

    private fun weatherCallback() = object : Callback<Weather> {
        override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
            val result = response.body()
            location.value = result!!.location.name

        }

        override fun onFailure(call: Call<Weather>, t: Throwable) {
            Timber.e("Weather on failure ${t.message}")
        }
    }
}