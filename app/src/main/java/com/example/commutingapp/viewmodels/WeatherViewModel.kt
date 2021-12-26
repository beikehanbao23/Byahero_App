package com.example.commutingapp.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.commutingapp.BuildConfig
import com.example.commutingapp.data.api.Weather
import com.example.commutingapp.data.api.WeatherService
import com.example.commutingapp.data.api.WeatherServiceAPI
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


class WeatherViewModel : ViewModel() {
    private var map: HashMap<String, String> = HashMap()
    private var request: WeatherServiceAPI
    private val searchedLocation = MutableLiveData<Weather>()
    private val jsonApi = MutableLiveData<String>()
    fun getWeatherJson(): LiveData<String> = jsonApi
    fun getSearchedLocation(): LiveData<Weather> = searchedLocation


    init {
        map.apply {
            this["key"] = BuildConfig.WEATHER_API_KEY
            this["days"] = "1"
            this["aqi"] = "yes"
            this["alerts"] = "yes"
        }
        request = WeatherService.buildService(WeatherServiceAPI::class.java)
    }

    private fun requestWeather(cityName: String) {
        map["q"] = cityName
        val call = request.getWeatherData(map)
        call.enqueue(weatherCallback())
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000L , 0.0f) { location = it }
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            location?.let {
                 geocoder.getFromLocation(it.latitude, it.longitude, 1).forEach { address ->
                    val city = "${address.locality} ${address.thoroughfare}"
                     requestWeather(city)
                     return@let
                }
            }
            throw RuntimeException("Location not found")
        } catch (e: IOException) {
            Timber.e("Weather View Model: ${e.message}")
        }
    }

    private fun weatherCallback() = object : Callback<Weather> {
        override fun onResponse(call: Call<Weather>, response: Response<Weather>) {

            if(!response.isSuccessful){
                Timber.e("Response not success: ${response.code()}")
                return
            }
            jsonApi.value = Gson().toJson(response.body())
            searchedLocation.value = response.body()

        }

        override fun onFailure(call: Call<Weather>, t: Throwable) {
            Timber.e("Weather View Model on failure ${t.message}")
        }
    }
}