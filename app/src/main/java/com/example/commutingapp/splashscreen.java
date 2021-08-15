package com.example.commutingapp;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import FirebaseUserManager.FirebaseUserManager;
import MenuButtons.CustomBackButton;
import UI.ActivitySwitcher;
import UI.ScreenDimension;

public class splashscreen extends AppCompatActivity  {

    private final int delayInMillis = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ScreenDimension(getWindow()).setWindowToFullScreen();
        setContentView(R.layout.activity_splashscreen);

        FirebaseUserManager.initializeFirebase();

    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUserManager.getCreatedUserAccount();
        if (FirebaseUserManager.userAlreadySignIn() && FirebaseUserManager.getFirebaseUserInstance().isEmailVerified()) {
            showMainScreenActivity();
            return;
        }
        new Handler().postDelayed(this::showIntroSlidersActivity, delayInMillis);
    }
    @Override
    public void onBackPressed() {

        new CustomBackButton(this,this).applyDoubleClickToExit();
    }

    private void showMainScreenActivity() {

        ActivitySwitcher.INSTANCE.startActivityOf(this,this,MainScreen.class);
    }
    private void showIntroSlidersActivity() {
        ActivitySwitcher.INSTANCE.startActivityOf(this,this,IntroSlider.class);
    }




    }



         