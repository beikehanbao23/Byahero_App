package com.example.commutingapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.commutingapp.BuildConfig
import com.example.commutingapp.data.model.WeatherDto
import com.example.commutingapp.data.model.WeatherServiceAPI

import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class WeatherViewModel @Inject constructor(
     private val request: WeatherServiceAPI
) : ViewModel() {
    private var map: HashMap<String, String> = HashMap()
    private val currentWeather = MutableLiveData<WeatherDto>()
    private val jsonApi = MutableLiveData<String>()
    private val exceptions = MutableLiveData<String>()
    fun getWeatherJson(): LiveData<String> = jsonApi
    fun getCurrentWeather(): LiveData<WeatherDto> = currentWeather
    fun getExceptions():LiveData<String> = exceptions

    init {
        map.apply {
            this["key"] = BuildConfig.WEATHER_API_KEY
            this["days"] = "1"
            this["aqi"] = "yes"
            this["alerts"] = "yes"
        }

    }

    fun requestWeather(cityName: String) {
        map["q"] = cityName
        val call = request.getWeatherData(map)
        call.enqueue(weatherCallback())
    }



    private fun weatherCallback() = object : Callback<WeatherDto> {
        override fun onResponse(call: Call<WeatherDto>, response: Response<WeatherDto>) {

            if(!response.isSuccessful){
                exceptions.postValue("Please check your internet connection or try again later. Error code: ${response.code()}")
            }
            jsonApi.value = Gson().toJson(response.body())
            currentWeather.value = response.body()

        }

        override fun onFailure(call: Call<WeatherDto>, t: Throwable) {
            exceptions.postValue("Please check your internet connection")
        }
    }
}