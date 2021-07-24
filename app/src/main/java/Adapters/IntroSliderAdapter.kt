package Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.commutingapp.R.drawable.*
import com.example.commutingapp.R.layout.intro_sliders_adapter
import com.example.commutingapp.R.string.*
import kotlinx.android.synthetic.main.intro_sliders_adapter.view.*

/*
This class setup the data that show in sliders
 */
 const val ITEMS_COUNT = 4

/* Adapters provide a binding from an
 app-specific data set to views that are
 displayed within a RecyclerView.*/

class IntroSliderAdapter(val context: Context) :
    RecyclerView.Adapter<IntroSliderAdapter.IntroSliderViewHolder>() {

    private val images = arrayListOf<Int>(
        rocket,
        enjoytrip,
        selectroute,
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

    inner class IntroSliderViewHolder(view: View) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroSliderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(intro_sliders_adapter, parent, false)
        return IntroSliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: IntroSliderViewHolder, position: Int) {

        fun elements() = object {
            val image = images[position]
            val title = context.getString(headerText[position])
            val description = context.getString(descriptionText[position])
        }
        holder.itemView.imageViewDisplaySliders.setImageResource(elements().image)
        holder.itemView.headerTextViewSliders.text = elements().title
        holder.itemView.descriptionTextViewSliders.text = elements().description
    }

    override  fun getItemCount() = ITEMS_COUNT
}