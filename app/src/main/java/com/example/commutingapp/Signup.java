package com.example.commutingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    public void backToSignIn(View view) {
        startActivity(new Intent(this,MainActivity.class));
        finish();

    }

    @Override
    public void onBackPressed() {
        backToSignIn(null);
    }
}