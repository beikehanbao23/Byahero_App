package com.example.commutingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.LinearLayout;

public class IntroSliders extends AppCompatActivity {
    private ViewPager2 viewPagerDisplay;
    private LinearLayout indicatorDots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_sliders);

        viewPagerDisplay = findViewById(R.id.displayViewPager);
        indicatorDots = findViewById(R.id.dotIndicatorLinearLayout);


    }
}