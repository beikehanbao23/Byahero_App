package com.example.commutingapp.views.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
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
import com.example.commutingapp.utils.others.TrackingPermissionUtility.hasLocationPermission
import com.example.commutingapp.utils.others.TrackingPermissionUtility.requestLocationPermission
import com.example.commutingapp.viewmodels.WeatherViewModel
import com.example.commutingapp.views.adapters.WeatherRecyclerViewAdapter
import com.example.commutingapp.views.ui.recycler_view.WeatherRecyclerViewModel
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



        if(!Connection.hasInternetConnection(requireContext())){
            binding!!.circularProgressBar.visibility = View.INVISIBLE
            val jsonObject:Weather = Gson().fromJson(getJsonSharedPreference(),Weather::class.java)
            renderData(jsonObject)
            return
        }

        if(hasLocationPermission(requireContext())){
            getUserCityLocation()
        }else{
            requestLocationPermission(this)
        }

    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun provideObservers(){


        weatherViewModel.getCurrentLocation().observe(viewLifecycleOwner){
            weatherViewModel.requestWeather(it)
        }
        weatherViewModel.getWeatherJson().observe(viewLifecycleOwner){
            updateJsonSharedPreference(it)
        }
        weatherViewModel.getSearchedLocation().observe(viewLifecycleOwner){
            renderData(it)
        }

    }


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun renderData(weatherInfo:Weather){
        binding!!.textViewCityName.text = "${weatherInfo.location.region}, ${weatherInfo.location.country}"
        binding!!.textViewTemperature.text = "${weatherInfo.current.temp_c}°c"
        binding!!.textViewWeatherCondition.text = weatherInfo.current.condition.text
        Picasso.Builder(activity).build().load("http:"+(weatherInfo.current.condition.icon) ).into(binding!!.imageViewWeatherCondition)
        weatherInfo.forecast.forecastday[0].hour.forEach{
            listOfWeatherModel.add(WeatherRecyclerViewModel(
                time = it.time,
                icon = it.condition.icon,
                temperature = it.temp_c.toString(),
                windSpeed = it.wind_kph.toString()
            ))
        }
        binding!!.recyclerViewDisplay.adapter?.notifyDataSetChanged()
        binding!!.circularProgressBar.visibility = View.INVISIBLE
    }


    private fun getUserCityLocation(){
        weatherViewModel.getLastKnownLocation(requireContext())
        binding!!.circularProgressBar.visibility = View.VISIBLE
    }

    private fun updateJsonSharedPreference(cityName:String){
        weatherApiJson.edit().putString(KEY_USER_CITY_JSON_WEATHER,cityName).apply()
    }
    private fun getJsonSharedPreference():String? = weatherApiJson.getString(KEY_USER_CITY_JSON_WEATHER,"")
    private fun initializeAttributes(){
        binding!!.recyclerViewDisplay.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding!!.recyclerViewDisplay.adapter = WeatherRecyclerViewAdapter(requireActivity(),listOfWeatherModel)
        weatherViewModel = WeatherViewModel()
    }
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION){
            getUserCityLocation()
            return
        }
    }
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Timber.e("onPermissionsDenied requestCode is $requestCode")
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }


}