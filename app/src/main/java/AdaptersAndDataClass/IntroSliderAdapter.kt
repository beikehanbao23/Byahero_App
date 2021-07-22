package AdaptersAndDataClass

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.commutingapp.R.drawable.*
import com.example.commutingapp.R.layout.intro_sliders_adapter
import com.example.commutingapp.R.string.*

const val ITEM_COUNT = 3

/* Adapters provide a binding from an
 app-specific data set to views that are
 displayed within a RecyclerView.*/

class IntroSliderAdapter(val context: Context) :
    RecyclerView.Adapter<IntroSliderAdapter.IntroSliderViewHolder>() {

    private val images = arrayListOf<Int>(
        enjoytrip,
        selectroute,
        accurate_weather
    )

    private val headerText = arrayListOf<Int>(
        headerTextEnjoyTripsMessage,
        headerTextChooseDestinationMessage,
        headerTextAccurateWeatherMessage
    )

    private val descriptionText = arrayListOf<Int>(
        descriptionTextEnjoyTripMessage,
        descriptionTextChooseDestinationMessage,
        descriptionTextAccurateWeatherMessage
    )



    /* A ViewHolder describes an
    item view and metadata about
    its place within the RecyclerView.*/

    inner  class IntroSliderViewHolder(view: View) : RecyclerView.ViewHolder(view)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroSliderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(intro_sliders_adapter, parent, false)
        return IntroSliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: IntroSliderViewHolder, position: Int) {
      val image = images[position]
    }

    override fun getItemCount() = ITEM_COUNT
}