package com.example.commutingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import IntroSlider.IntroSliderAdapter;


public class IntroSliders extends AppCompatActivity {


    ViewPager2 viewPager2;
    LinearLayout linearLayoutDotsIndicator;
    Button skipButton,backButton,nextButton;
    TextView makeDots[];
    IntroSliderAdapter introSliderAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_sliders);

        skipButton = findViewById(R.id.skipButtonSliders);
        backButton = findViewById(R.id.backButtonSliders);
        nextButton = findViewById(R.id.nextButtonSliders);
        viewPager2 = findViewById(R.id.viewPagerSliders);
        linearLayoutDotsIndicator = findViewById(R.id.linearLayout_dotsIndicator);
        introSliderAdapter = new IntroSliderAdapter(this);
        viewPager2.setAdapter(introSliderAdapter);
    }

    public void skipButtonSlidersIsClicked(View view) {

    }

    public void nextButtonSlidersIsClicked(View view) {

    }

    public void backButtonSlidersIsClicked(View view) {

    }
}