package com.example.commutingapp.views.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commutingapp.R
import com.example.commutingapp.databinding.RvWeatherAdapterBinding

import com.example.commutingapp.views.ui.recycler_view_model.WeatherRVModel
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class WeatherAdapter(
    private val activity: Activity,
    private val listOfWeatherModel:List<WeatherRVModel>
):RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    private lateinit var binding: RvWeatherAdapterBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherAdapter.ViewHolder {
        binding = RvWeatherAdapterBinding.inflate(activity.layoutInflater,parent,false)
        return ViewHolder(binding.root)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: WeatherAdapter.ViewHolder, position: Int) {
        holder.bindAttributes(position)
    }


    override fun getItemCount(): Int  = listOfWeatherModel.size


     inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
         @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "SimpleDateFormat")

         fun bindAttributes(position: Int) {
             val model: WeatherRVModel = listOfWeatherModel[position]

             val dateFormatInput = SimpleDateFormat("yyyy-MM-dd hh:mm")
             val dateFormatOutput = SimpleDateFormat("hh:mm aa")

             with(binding) {
                 try {
                     val date: Date? = dateFormatInput.parse(model.time)
                     textViewCardTime.text = dateFormatOutput.format(date)
                 } catch (e: ParseException) {
                     Timber.e("Parse Exception in ${this.javaClass.name}: ${e.message}")
                 }


                 textViewCardTemperature.text = model.temperature + "°c"
                 textViewCardWindSpeed.text = model.windSpeed + "Km/h"


                 if (model.icon != null) {
                     Glide.with(activity).load("http:${model.icon}").into(imageViewCardWeatherCondition)
                     return
                 }
                 Glide.with(activity).load(R.drawable.default_weather_icon).into(imageViewCardWeatherCondition)
             }
         }
    }
}