package IntroSlider;

import android.content.Context;
import android.view.View;
import androidx.viewpager.widget.PagerAdapter;

import com.example.commutingapp.R;

public class IntroSliderAdapter extends PagerAdapter {


    private Context context;

    public IntroSliderAdapter(Context context){
        this.context = context;
    }

    private Integer[] headerText = {
            R.string.headerTextChooseDestinationMessage,
            R.string.headerTextEnjoyTripsMessage,
           R.string.headerTextAccurateWeatherMessage,
    };

    private Integer[] descriptionText = {
            R.string.
    };

    private Integer[] imageDisplay = {

    };


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}
