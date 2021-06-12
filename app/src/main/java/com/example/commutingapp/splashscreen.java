package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import FirebaseUserManager.FirebaseUserRegister;
import MenuButtons.BackButton;

public class splashscreen extends AppCompatActivity {
    private final Handler handler = new Handler();
    private BackButton backButton;
    private FirebaseUserRegister firebaseUserRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backButton = new BackButton(this.getBaseContext(), 2000, "Tap again to exit");
        setContentView(R.layout.activity_splashscreen);

        firebaseUserRegister = new FirebaseUserRegister();
        firebaseUserRegister.initializeFirebase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUserRegister.getCurrentUser();
        if (firebaseUserRegister.isUserAlreadySignedIn()) {
           handler.postDelayed(() -> this.startActivity(new Intent(this, MainScreen.class)), 2500);
           return;
       }
        handler.postDelayed(() -> this.startActivity(new Intent(this, MainActivity.class)), 2500);

    }

    @Override
    public void onBackPressed() {
        backButton.backButtonisPressed();
    }


}