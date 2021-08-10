package Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.commutingapp.R.drawable.*
import com.example.commutingapp.R.string.*
import com.example.commutingapp.databinding.IntroSlidersAdapterBinding


const val ITEMS_COUNT = 4

/* Adapters provide a binding from an
 app-specific data set to views that are
 displayed within a RecyclerView.*/

class IntroSliderAdapter(val inflater: LayoutInflater, val _context: Context) :
    RecyclerView.Adapter<IntroSliderAdapter.IntroSliderViewHolder>() {

    private var binding: IntroSlidersAdapterBinding? = null
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


    /*
    Executing a lambda on non-null objects: let
    Introducing an expression as a variable in local scope: let


    Object configuration: apply
    Use the apply() function if you are not accessing any functions of the receiver within your block, and also want to return the same receiver.


    Object configuration and computing the result: run
    Running statements where an expression is required: non-extension run





    Additional effects: also
    Use the also() function, if your block does not access its receiver parameter at all,
    or if it does not mutate its receiver parameter. Don’t use also() if your block needs to return a different value.







    Grouping function calls on an object: with
    Use with() only on non-nullable receivers, and when you don’t need its result.

    val person: Person = getPerson()
    with(person) {
    print(name)
    print(age)
}

    The equivalent code without with() looks like this:

    val person: Person = getPerson()
    print(person.name)
    print(person.age)
     */







    /* A ViewHolder describes an
    item view and metadata about
    its place within the RecyclerView.*/

    inner class IntroSliderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindAttributes(position: Int) {
            binding?.imageViewDisplaySliders?.setImageResource(elements(position).image)
            binding?.headerTextViewSliders?.text = elements(position).header
            binding?.descriptionTextViewSliders?.text = elements(position).description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroSliderViewHolder {

        binding = IntroSlidersAdapterBinding.inflate(inflater, parent, false)

        return IntroSliderViewHolder(binding!!.root)

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

    fun destroyIntroSliderAdapterBinding() {
        binding = null
    }
}