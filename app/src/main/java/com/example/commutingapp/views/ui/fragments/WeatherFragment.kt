package com.example.commutingapp.views.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.commutingapp.R
import com.example.commutingapp.data.api.Weather
import com.example.commutingapp.databinding.WeatherFragmentBinding
import com.example.commutingapp.utils.InternetConnection.Connection
import com.example.commutingapp.utils.others.Constants.KEY_USER_CITY_JSON_WEATHER
import com.example.commutingapp.utils.others.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.commutingapp.utils.others.Constants.REQUEST_USER_LOCATION
import com.example.commutingapp.utils.others.TrackingPermissionUtility.hasLocationPermission
import com.example.commutingapp.utils.others.TrackingPermissionUtility.requestLocationPermission
import com.example.commutingapp.viewmodels.WeatherViewModel
import com.example.commutingapp.views.adapters.WeatherAdapter
import com.example.commutingapp.views.ui.recycler_view.WeatherRecyclerViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber


@AndroidEntryPoint
class WeatherFragment: Fragment(R.layout.weather_fragment), EasyPermissions.PermissionCallbacks{
    private var binding: WeatherFragmentBinding? = null
    private var listOfWeatherModel:MutableList<WeatherRecyclerViewModel> = ArrayList()
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var weatherApiJson :SharedPreferences
    override fun onAttach(context: Context) {
        super.onAttach(context)
        weatherApiJson = context.getSharedPreferences(KEY_USER_CITY_JSON_WEATHER,Context.MODE_PRIVATE)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = WeatherFragmentBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAttributes()
        provideObservers()

        if(!Connection.hasGPSConnection(requireContext())){
            checkLocationSetting().addOnCompleteListener(::askGPS)
        }

        if(!Connection.hasInternetConnection(requireContext())){
            renderDefaultData()
            return
       }
        checkLocationPermission()

    }
    private fun checkLocationPermission(){
        if(hasLocationPermission(requireContext())){
            getUserCityLocation()
        }else{
            requestLocationPermission(this)
        }
    }
    private fun renderDefaultData(){


            binding!!.circularProgressBar.visibility = View.INVISIBLE
            val jsonObject:Weather? = Gson().fromJson(getJsonSharedPreference(),Weather::class.java)
            if(jsonObject!=null){
                renderData(jsonObject)
                return
            }
            renderDefaultRecyclerViewsData()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun renderDefaultRecyclerViewsData(){
        for(i in 1..12){
            listOfWeatherModel.add(
                WeatherRecyclerViewModel(
                time = "--:-- --",
                icon = null,
                temperature = "00.0°c",
                windSpeed = "---/h --")
            )
        }
        binding!!.recyclerViewDisplay.adapter?.notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun provideObservers(){

        weatherViewModel.getWeatherJson().observe(viewLifecycleOwner){
            updateJsonSharedPreference(it)
            binding!!.circularProgressBar.visibility = View.INVISIBLE

        }
        weatherViewModel.getSearchedLocation().observe(viewLifecycleOwner){
            renderData(it)
            binding!!.circularProgressBar.visibility = View.INVISIBLE
        }


    }

    override fun onRequestPermissionsResult( requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun renderData(weatherInfo:Weather){
        with(binding!!) {
            with(weatherInfo) {
                with(current){
                    textViewCityName.text = "${location.region}, ${location.country}"
                    textViewTemperature.text = "${temp_c}°c"
                    textViewWeatherCondition.text = condition.text
                    textViewCloud.text = "   Cloud: ${cloud}%"
                    textViewHumidity.text = "   Humidity: ${humidity}%"
                    textViewInformation.text = "${temp_c}°c | Feels like ${feelslike_c}°c"
                    Picasso.Builder(activity).build()
                        .load("http:" + (condition.icon))
                        .into(imageViewWeatherCondition)
                    textViewWindSpeed.text = "   Wind: ${wind_kph}km/h"
                }

                with(forecast.forecastday[0]){
                    textViewChanceOfRain.text = "   Chance of rain: ${day.daily_chance_of_rain}%"
                    textViewhighestLowestTemperature.text = "H: ${day.maxtemp_c}°c  |  L: ${day.mintemp_c}°c"

                    hour.forEach {
                        //todo add log for time

                        Timber.e("${it.time}\n")
                        listOfWeatherModel.add(
                            WeatherRecyclerViewModel(
                                time = it.time,
                                icon = it.condition.icon,
                                temperature = it.temp_c.toString(),
                                windSpeed = it.wind_kph.toString()
                            )
                        )
                    }
                    recyclerViewDisplay.adapter?.notifyDataSetChanged()
                }


            }
        }
    }


    private fun getUserCityLocation(){
        try {
            weatherViewModel.getLastKnownLocation(requireContext())
            binding!!.circularProgressBar.visibility = View.VISIBLE
        }catch (e:RuntimeException){
            renderDefaultData()
            binding!!.circularProgressBar.visibility = View.INVISIBLE

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_USER_LOCATION && resultCode == RESULT_OK){
            getUserCityLocation()
        }
    }

    private fun checkLocationSetting():Task<LocationSettingsResponse>{

        return LocationServices.getSettingsClient(requireContext())
            .checkLocationSettings(
                LocationSettingsRequest.Builder()
                .addLocationRequest(CommuterFragment.request)
                .setAlwaysShow(true)
                .build())

    }
    private fun askGPS(task: Task<LocationSettingsResponse>){

        try {
            task.getResult(ApiException::class.java)
        } catch (e: ApiException) {
            handleLocationResultException(e)
        }

    }
    private fun handleLocationResultException(e: ApiException) {
        when (e.statusCode) {
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                (e as ResolvableApiException).apply {
                    startIntentSenderForResult(this.resolution.intentSender, REQUEST_USER_LOCATION, null, 0, 0, 0, null)
                } }
        }
    }
    private fun updateJsonSharedPreference(cityName:String){
        weatherApiJson.edit().putString(KEY_USER_CITY_JSON_WEATHER,cityName).apply()
    }
    private fun getJsonSharedPreference():String? = weatherApiJson.getString(KEY_USER_CITY_JSON_WEATHER,null)
    private fun initializeAttributes(){
        binding!!.recyclerViewDisplay.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding!!.recyclerViewDisplay.adapter?.setHasStableIds(true)
        binding!!.recyclerViewDisplay.adapter = WeatherAdapter(requireActivity(),listOfWeatherModel)
        weatherViewModel = WeatherViewModel()
    }
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION){
           // getUserCityLocation()
            return
        }
    }
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            AppSettingsDialog.Builder(this).build().show()
           // renderDefaultData()
        }
    }


}