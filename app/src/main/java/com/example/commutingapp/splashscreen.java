package com.example.commutingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import FirebaseUserManager.FirebaseUserManager;
import Logger.CustomToastMessage;
import MenuButtons.ButtonClicksTimeDelay;
import MenuButtons.CustomBackButton;
import static com.example.commutingapp.R.string.*;
public class splashscreen extends AppCompatActivity implements CustomBackButton {
    private  Handler handler;
    private ButtonClicksTimeDelay backButtonClick;

    private final int delayInMillis = 700;
    private CustomToastMessage toastMessageBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        handler = new Handler();
        backButtonClick = new ButtonClicksTimeDelay(2000);
        toastMessageBackButton = new CustomToastMessage(this,getString(getDoubleTappedMessage),10);


        FirebaseUserManager.initializeFirebase();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUserManager.getCurrentUser();
        if (FirebaseUserManager.isUserAlreadySignedIn() && FirebaseUserManager.getFirebaseUser().isEmailVerified()) {
            showMainScreen();
            return;
        }

        handler.postDelayed(() -> {
            showSignIn();
        }, delayInMillis);
    }


    @Override
    public void onBackPressed() {
        backButtonClicked();
    }


    private void showMainScreen(){
        startActivity(new Intent(this, MainScreen.class));
        finish();
    }
    private void showSignIn(){
        startActivity(new Intent(this, SignIn.class));
        finish();
    }

    @Override
    public void backButtonClicked() {

        CustomBackButton customBackButton = ()->{
            if(backButtonClick.isDoubleTapped()){
                toastMessageBackButton.hideToast();
                super.onBackPressed();
                return;
            }
            toastMessageBackButton.showToast();
            backButtonClick.registerFirstClick();
        };
        customBackButton.backButtonClicked();
    }
}