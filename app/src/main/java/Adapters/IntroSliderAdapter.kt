package Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.commutingapp.R.drawable.*
import com.example.commutingapp.R.string.*
import com.example.commutingapp.databinding.IntroSlidersAdapterBinding


/*
This class setup the data that show in sliders
 */



const val ITEMS_COUNT = 4

/* Adapters provide a binding from an
 app-specific data set to views that are
 displayed within a RecyclerView.*/

class IntroSliderAdapter(val inflater: LayoutInflater,val _context: Context) :
    RecyclerView.Adapter<IntroSliderAdapter.IntroSliderViewHolder>() {

    private lateinit var binding: IntroSlidersAdapterBinding
    private val images = arrayListOf<Int>(
        rocket,
        enjoytrip,
        point,
        accurate_weather
    )
    private val headerText = arrayListOf<Int>(
        headerTextCreateAccountMessage,
        headerTextEnjoyTripsMessage,
        headerTextChooseDestinationMessage,
        headerTextAccurateWeatherMessage
    )
    private val descriptionText = arrayListOf<Int>(
        descriptionTextCreateAccountMessage,
        descriptionTextEnjoyTripMessage,
        descriptionTextChooseDestinationMessage,
        descriptionTextAccurateWeatherMessage

    )

    /* A ViewHolder describes an
    item view and metadata about
    its place within the RecyclerView.*/

    inner class IntroSliderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindAttributes(position: Int){
            binding.imageViewDisplaySliders.setImageResource(elements(position).image)
            binding.headerTextViewSliders.text = elements(position).header
            binding.descriptionTextViewSliders.text = elements(position).description
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroSliderViewHolder {

         binding = IntroSlidersAdapterBinding.inflate(inflater,parent,false);
        return IntroSliderViewHolder(binding.root)
    }


    override fun onBindViewHolder(holder: IntroSliderViewHolder, position: Int) {
        holder.bindAttributes(position)
    }

    private fun elements(position: Int) = object {

        val image = images[position]
        val header = _context.getString(headerText[position])
        val description = _context.getString(descriptionText[position])
    }
    override fun getItemCount() = ITEMS_COUNT
}