package com.example.commutingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.UserInfo;

import FirebaseUserManager.FirebaseUserManager;
import Logger.CustomToastMessage;
import MenuButtons.CustomBackButton;
import MenuButtons.backButton;
import MenuButtons.BackButtonDoubleClicked;

public class MainScreen extends AppCompatActivity implements BackButtonDoubleClicked {

    private CustomToastMessage toastMessageBackButton;
    private TextView nameTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_screen);
        toastMessageBackButton = new CustomToastMessage(this,getString(R.string.doubleTappedMessage),10);
        nameTextView = findViewById(R.id.nameTextView);
        FirebaseUserManager.initializeFirebase();
        checkFacebookTokenIfExpired();
        setNameToTextView();
    }

    private void setNameToTextView(){
        for (UserInfo userInfo : FirebaseUserManager.getFirebaseUserInstance().getProviderData()) {
            if (userInfo.getProviderId().equals("facebook.com")) {
                Log.d("TAG", "User is signed in with Facebook");
                nameTextView.setText(FirebaseUserManager.getFirebaseUserInstance().getDisplayName());
            }else {
                //TODO filter email address
            }
        }
    }
    private void checkFacebookTokenIfExpired(){

       new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    signOutUser();
                }
            }
        };
    }

    public void LogoutButtonClicked(View view) {
       signOutUser();
    }

    //TODO
    private void signOutUser(){
        LoginManager.getInstance().logOut();
        FirebaseUserManager.getFirebaseAuthInstance().signOut();
        startActivity(new Intent(this, SignIn.class));
        finish();
    }
    @Override
    public void onBackPressed() {
      backButtonClicked();
    }

    @Override
    public void backButtonClicked() {


        new CustomBackButton(()->{
            if(backButton.isDoubleTapped()){
                toastMessageBackButton.hideToast();
                super.onBackPressed();
                return;
            }
            toastMessageBackButton.showToast();
            backButton.registerFirstClick();
        }).backButtonIsClicked();

    }
}