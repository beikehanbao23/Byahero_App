package com.example.commutingapp.views.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.commutingapp.R
import com.example.commutingapp.databinding.WeatherFragmentBinding
import com.example.commutingapp.views.adapters.WeatherRecyclerViewAdapter
import com.example.commutingapp.views.recycler_view.WeatherRecyclerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherFragment: Fragment(R.layout.weather_fragment){
    private var binding: WeatherFragmentBinding? = null
    private lateinit var listOfWeatherModel:List<WeatherRecyclerViewModel>



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = WeatherFragmentBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            listOfWeatherModel = ArrayList<WeatherRecyclerViewModel>()
            binding!!.recyclerViewDisplay.adapter = WeatherRecyclerViewAdapter(requireContext(),layoutInflater,listOfWeatherModel)
    }


    private fun getWeatherInfo(cityName:String){

    }
}