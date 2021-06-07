package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import MenuButtons.BackButton;

public class MainActivity extends AppCompatActivity {
    private BackButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new splashscreen().finish();
        backButton = new BackButton(this.getBaseContext(), 2000, "Tap again to exit");
    }


    public void SignUpTextClicked(View view) {
        this.startActivity(new Intent(this, Signup.class));
        finish();
    }

    @Override
    public void onBackPressed() {
       backButton.backButtonisPressed();
    }
}