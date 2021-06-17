package com.example.commutingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import FirebaseUserManager.FirebaseUserManager;

public class MainScreen extends AppCompatActivity {
    private FirebaseUserManager firebaseUserManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        firebaseUserManager = new FirebaseUserManager();
        firebaseUserManager.initializeFirebase();


    }



    public void LogoutButtonClicked(View view) {
        firebaseUserManager.getFirebaseAuthenticate().signOut();
        startActivity(new Intent(this, SignIn.class));
        finish();
    }
}