package com.example.commutingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import FirebaseUserManager.FirebaseUserManager;

public class MainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
    }

    public void LogoutButtonClicked(View view) {
        FirebaseUserManager firebaseUserManager = new FirebaseUserManager();
        firebaseUserManager.signOutUserAccount();
        startActivity(new Intent(this, SignIn.class));
        finish();
    }
}