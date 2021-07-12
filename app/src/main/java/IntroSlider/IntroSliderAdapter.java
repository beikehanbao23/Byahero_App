package IntroSlider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.commutingapp.R;

public class IntroSliderAdapter extends RecyclerView.Adapter {


    private Context context;
    private ImageView photoDisplay;
    private TextView headerTextDisplay,descriptionTextDisplay;

    public IntroSliderAdapter(Context context) {
        this.context = context;
    }
    private final Integer[] headerText = {
            R.string.headerTextChooseDestinationMessage,
            R.string.headerTextEnjoyTripsMessage,
            R.string.headerTextAccurateWeatherMessage,
    };


    private final Integer[] descriptionText = {
            R.string.descriptionTextChooseDestinationMessage,
            R.string.descriptionTextEnjoyTripMessage,
            R.string.descriptionTextAccurateWeatherMessage
    };


    private final Integer[] imageDisplay = {
            R.drawable.selectroute,
            R.drawable.bus_gif,
            R.drawable.accurate_weather
    };



    @Override
    public int getCount() {
        return descriptionText.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (ConstraintLayout) object;
    }


    @Override
    public Object instantiateItem( ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.placeholder_sliders,container,false);

        photoDisplay = view.findViewById(R.id.imageViewDisplaySliders);
        headerTextDisplay = view.findViewById(R.id.headerTextViewSliders);
        descriptionTextDisplay = view.findViewById(R.id.descriptionTextViewSliders);

        photoDisplay.setImageResource(imageDisplay[position]);
        headerTextDisplay.setText(headerText[position]);
        descriptionTextDisplay.setText(descriptionText[position]);

        container.addView(view);
     return view;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
