package com.example.commutingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.*;

import IntroSlider.IntroSliderAdapter;


public class IntroSliders extends AppCompatActivity {


    ViewPager2 viewPager2;
    LinearLayout linearLayout;
    Button skipButton,backButton,nextButton;
    TextView dots[];
    IntroSliderAdapter introSliderAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_sliders);

        skipButton = findViewById(R.id.skipButtonSliders);
        backButton = findViewById(R.id.backButtonSliders);
        nextButton = findViewById(R.id.nextButtonSliders);
    }
}