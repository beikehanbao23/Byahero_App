package com.example.commutingapp.views.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.commutingapp.databinding.WeatherRecyclerViewAdapterBinding
import com.example.commutingapp.views.recycler_view.WeatherRecyclerViewModel
import com.squareup.picasso.Picasso
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class WeatherRecyclerViewAdapter(
    private val context: Context,
    private val inflater:LayoutInflater,
    private val listOfWeatherModel:List<WeatherRecyclerViewModel>
):RecyclerView.Adapter<WeatherRecyclerViewAdapter.ViewHolder>() {
    private var binding: WeatherRecyclerViewAdapterBinding? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherRecyclerViewAdapter.ViewHolder {
        binding = WeatherRecyclerViewAdapterBinding.inflate(inflater,parent,false)
        return ViewHolder(binding!!.root)
    }


    override fun onBindViewHolder(holder: WeatherRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bindAttributes(position)
    }

    override fun getItemCount(): Int  = listOfWeatherModel.size


     inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
         @SuppressLint("SetTextI18n")

         fun bindAttributes(position: Int){
            val model:WeatherRecyclerViewModel = listOfWeatherModel[position]

            val dateFormatInput = SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.US)
            val dateFormatOutput = SimpleDateFormat("hh:mm aa",Locale.getDefault())

            try{
                val date:Date? = dateFormatInput.parse(model.time)
                binding!!.textViewCardTime.text = dateFormatOutput.format(date)
            }catch (e:ParseException){
                Timber.e("Parse Exception in ${this.javaClass.name}: ${e.message}")
            }
            binding!!.textViewCardTemperature.text = model.temperature + "°c"
            binding!!.textViewCardWindSpeed.text = model.windSpeed + "Km/h"
            Picasso.Builder(context).build().load("http:"+(model.icon) ).into(binding!!.imageViewCardWeatherCondition)
        }
    }
}