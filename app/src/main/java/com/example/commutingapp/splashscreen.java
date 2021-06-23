package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import FirebaseUserManager.FirebaseUserManager;
import Logger.CustomToastMessage;
import MenuButtons.ButtonClicksTimeDelay;
import MenuButtons.CustomBackButton;

public class splashscreen extends AppCompatActivity implements CustomBackButton {
    private final Handler handler = new Handler();
    private ButtonClicksTimeDelay backButtonClick;
    private FirebaseUserManager firebaseUserManager;
    private final int delayInMillis = 700;
    private CustomToastMessage toastMessageBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        backButtonClick = new ButtonClicksTimeDelay(2000);
        toastMessageBackButton = new CustomToastMessage(this,"Tap again to exit",10);

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
                toastMessageBackButton.hideMessage();
                super.onBackPressed();
                return;
            }
            toastMessageBackButton.showMessage();
            backButtonClick.registerFirstClick();
        };
        customBackButton.backButtonClicked();
    }
}