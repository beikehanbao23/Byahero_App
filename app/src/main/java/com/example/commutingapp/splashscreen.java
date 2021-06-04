package com.example.commutingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

public class splashscreen extends AppCompatActivity {
private final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        handler.postDelayed(()->setContentView(R.layout.activity_main),2500);
    }
}