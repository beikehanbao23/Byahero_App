package IntroSlider;

import android.content.Context;
import android.view.View;

import androidx.viewpager.widget.PagerAdapter;

import com.example.commutingapp.R;

public class IntroSliderAdapter extends PagerAdapter {


    private final Context context;
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

    public IntroSliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}
