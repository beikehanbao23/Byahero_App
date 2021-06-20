package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import FirebaseUserManager.FirebaseUserManager;
import MenuButtons.Clicks_BackButton;

public class splashscreen extends AppCompatActivity {
    private final Handler handler = new Handler();
    private Clicks_BackButton backButton;
    private FirebaseUserManager firebaseUserManager;
    private final int delayInMillis = 750;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        backButton = new Clicks_BackButton(this.getBaseContext(),"Tap again to exit");
        firebaseUserManager = new FirebaseUserManager();
        firebaseUserManager.initializeFirebase();
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUserManager.getCurrentUser();
        if (firebaseUserManager.isUserAlreadySignedIn()) {
            showMainScreen();
            return;
        }

        handler.postDelayed(() -> {
            showSignIn();
        }, delayInMillis);
    }

    @Override
    public void onBackPressed() {
        backButton.showToastMessageThenBack();
    }


    private void showMainScreen(){
        startActivity(new Intent(this, MainScreen.class));
        finish();
    }
    private void showSignIn(){
        startActivity(new Intent(this, SignIn.class));
        finish();
    }
}