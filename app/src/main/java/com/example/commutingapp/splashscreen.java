package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import FirebaseUserManager.FirebaseUserManager;
import MenuButtons.BackButton;

public class splashscreen extends AppCompatActivity {
    private final Handler handler = new Handler();
    private BackButton backButton;
    private FirebaseUserManager firebaseUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backButton = new BackButton(this.getBaseContext(), 2000, "Tap again to exit");
        setContentView(R.layout.activity_splashscreen);

        firebaseUserManager = new FirebaseUserManager();
        firebaseUserManager.initializeFirebase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUserManager.getCurrentUser();
        if (firebaseUserManager.isUserAlreadySignedIn()) {
           handler.postDelayed(() -> this.startActivity(new Intent(this, MainScreen.class)), 2500);
           return;
       }
        handler.postDelayed(() -> this.startActivity(new Intent(this, SignIn.class)), 2500);

    }

    @Override
    public void onBackPressed() {
        backButton.backButtonisPressed();
    }


}