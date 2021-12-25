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
import com.example.commutingapp.databinding.WeatherFragmentBinding
import com.example.commutingapp.utils.InternetConnection.Connection
import com.example.commutingapp.utils.others.Constants.KEY_USER_CITY_LOCATION_SHARED_PREFERENCE
import com.example.commutingapp.utils.others.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.commutingapp.utils.others.TrackingPermissionUtility.hasLocationPermission
import com.example.commutingapp.utils.others.TrackingPermissionUtility.requestLocationPermission
import com.example.commutingapp.viewmodels.WeatherViewModel
import com.example.commutingapp.views.adapters.WeatherRecyclerViewAdapter
import com.example.commutingapp.views.dialogs.DialogDirector
import com.example.commutingapp.views.ui.recycler_view.WeatherRecyclerViewModel
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
    private  var ui: HashMap<String, SharedPreferences> = HashMap()
    private lateinit var userCityLocation :SharedPreferences
    override fun onAttach(context: Context) {
        super.onAttach(context)
        userCityLocation = context.getSharedPreferences(KEY_USER_CITY_LOCATION_SHARED_PREFERENCE,Context.MODE_PRIVATE)

        /*
        ui = HashMap()
        ui["userCity"] = context.getSharedPreferences(KEY_USER_CITY_LOCATION_SHARED_PREFERENCE,Context.MODE_PRIVATE)
        ui["temperature"] = context.getSharedPreferences(KEY_USER_CITY_LOCATION_TEMPERATURE,Context.MODE_PRIVATE)
        ui["weatherCondition"] = context.getSharedPreferences(KEY_USER_CITY_WEATHER_CONDITION_TEXT,Context.MODE_PRIVATE)
        ui["weatherIcon"] = context.getSharedPreferences(KEY_USER_CITY_WEATHER_CONDITION_ICON,Context.MODE_PRIVATE)


         */
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
             DialogDirector(requireActivity()).buildNoInternetDialog()
            binding!!.circularProgressBar.visibility = View.INVISIBLE
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
            updateUserCityNameToSharedPreference(it)
        }

        weatherViewModel.getSearchedLocation().observe(viewLifecycleOwner){weatherInfo->
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
    }



    private fun getUserCityLocation(){
        weatherViewModel.getLastKnownLocation(requireContext())
       val currentCity = getUserCityNameFromSharedPreference()
        currentCity?.let(weatherViewModel::requestWeather)
        binding!!.circularProgressBar.visibility = View.VISIBLE
    }

    private fun updateUserCityNameToSharedPreference(cityName:String){
        userCityLocation.edit().putString(KEY_USER_CITY_LOCATION_SHARED_PREFERENCE,cityName).apply()
    }
    private fun getUserCityNameFromSharedPreference():String? = userCityLocation.getString(KEY_USER_CITY_LOCATION_SHARED_PREFERENCE,"")
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