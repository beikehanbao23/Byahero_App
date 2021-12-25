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
    private val userLocation = MutableLiveData<String>()
    private val searchedLocation = MutableLiveData<Weather>()
    fun getSearchedLocation(): LiveData<Weather> = searchedLocation
    fun getCurrentLocation(): LiveData<String> = userLocation

    init {
        map.apply {
            this["key"] = BuildConfig.WEATHER_API_KEY
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

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            location?.let {
                val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                addresses.forEach { address ->
                    val city = "${address.locality} ${address.thoroughfare}"
                    userLocation.value = city
                }
            }
        } catch (e: IOException) {
            Timber.e("Weather View Model: ${e.message}")
        }
    }

    private fun weatherCallback() = object : Callback<Weather> {
        override fun onResponse(call: Call<Weather>, response: Response<Weather>) {

            if(!response.isSuccessful){
                Timber.e("Response no success: ${response.code()}")
                return
            }
             searchedLocation.value = response.body()
        }

        override fun onFailure(call: Call<Weather>, t: Throwable) {
            Timber.e("Weather View Model on failure ${t.message}")
        }
    }
}