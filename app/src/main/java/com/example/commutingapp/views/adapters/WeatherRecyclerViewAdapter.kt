package com.example.commutingapp.views.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.commutingapp.R
import com.example.commutingapp.databinding.WeatherRecyclerViewAdapterBinding
import com.example.commutingapp.views.ui.recycler_view.WeatherRecyclerViewModel
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class WeatherRecyclerViewAdapter(
    private val activity: Activity,
    private val listOfWeatherModel:List<WeatherRecyclerViewModel>
):RecyclerView.Adapter<WeatherRecyclerViewAdapter.ViewHolder>() {

    private var binding: WeatherRecyclerViewAdapterBinding? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherRecyclerViewAdapter.ViewHolder {
        binding = WeatherRecyclerViewAdapterBinding.inflate(activity.layoutInflater,parent,false)
        return ViewHolder(binding!!.root)
    }


    override fun onBindViewHolder(holder: WeatherRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bindAttributes(position)
    }

    override fun getItemCount(): Int  = listOfWeatherModel.size


     inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
         @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")

         fun bindAttributes(position: Int){
            val model: WeatherRecyclerViewModel = listOfWeatherModel[position]

            val dateFormatInput = SimpleDateFormat("yyyy-MM-dd hh:mm")
            val dateFormatOutput = SimpleDateFormat("hh:mm aa")

            try{
                val date:Date? = dateFormatInput.parse(model.time)
                binding!!.textViewCardTime.text = dateFormatOutput.format(date)
            }catch (e:ParseException){
                Timber.e("Parse Exception in ${this.javaClass.name}: ${e.message}")
            }


            binding!!.textViewCardTemperature.text = model.temperature + "°c"
            binding!!.textViewCardWindSpeed.text = model.windSpeed + "Km/h"


             if(model.icon != null){
                Picasso.Builder(activity).build().load("http:"+(model.icon) ).into(binding!!.imageViewCardWeatherCondition)
                return
             }
             Picasso.Builder(activity).build().load(R.drawable.default_weather_icon).into(binding!!.imageViewCardWeatherCondition)
        }
    }
}