package AdaptersAndDataClass

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.commutingapp.R

class IntroSliderAdapter(private val introslides: List<IntroSlider_DC>) : RecyclerView.Adapter<IntroSliderAdapter.IntroSliderViewHolder>() {


    inner class IntroSliderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val textTitle = view.findViewById<TextView>(R.id.headerTextViewSliders)
        private val textDescription = view.findViewById<TextView>(R.id.descriptionTextViewSliders)
        private val imageDisplay = view.findViewById<ImageView>(R.id.imageViewDisplaySliders)

    fun bind(introSlider:IntroSlider_DC){
        textTitle.text = introSlider.title
        textDescription.text = introSlider.description
        imageDisplay.setImageResource(introSlider.icon)
    }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroSliderViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: IntroSliderViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}