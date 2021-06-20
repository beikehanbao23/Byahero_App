package com.example.commutingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import FirebaseUserManager.FirebaseUserManager;
import MenuButtons.Clicks_BackButton;

public class MainScreen extends AppCompatActivity {
    private FirebaseUserManager firebaseUserManager;
    private Clicks_BackButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        firebaseUserManager = new FirebaseUserManager();
        firebaseUserManager.initializeFirebase();
        backButton = new Clicks_BackButton(this.getBaseContext(),  "Tap again to exit");
    }



    public void LogoutButtonClicked(View view) {
        firebaseUserManager.getFirebaseAuthenticate().signOut();
        startActivity(new Intent(this, SignIn.class));
        finish();
    }

    @Override
    public void onBackPressed() {
      backButton.showToastMessageThenBack();
    }
}