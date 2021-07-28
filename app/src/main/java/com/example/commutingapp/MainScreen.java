package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.UserInfo;

import FirebaseUserManager.FirebaseUserManager;
import Logger.CustomToastMessage;
import MenuButtons.BackButtonDoubleClicked;
import MenuButtons.CustomBackButton;
import MenuButtons.backButton;

public class MainScreen extends AppCompatActivity implements BackButtonDoubleClicked {

    private CustomToastMessage toastMessageBackButton;
    private TextView nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_screen);
        toastMessageBackButton = new CustomToastMessage(this, getString(R.string.doubleTappedMessage), 10);
        nameTextView = findViewById(R.id.nameTextView);
        FirebaseUserManager.initializeFirebase();
        checkFacebookTokenIfExpired();
        setNameToTextView();
        Log.e("WATCH THIS","EXECUTING AGAIN");
    }

    private void setNameToTextView() {
        for (UserInfo userInfo : FirebaseUserManager.getFirebaseUserInstance().getProviderData()) {
            if (userInfo.getProviderId().equals("facebook.com")) {
                Log.e("MAINSCREEN:", "User is signed in with Facebook");
                nameTextView.setText(FirebaseUserManager.getFirebaseUserInstance().getDisplayName());
            } else {
                //TODO filter email address
            }
        }
    }
    //TODO Add popup dialog here
    private void checkFacebookTokenIfExpired() {
        new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                //Add dialog
                    Log.e(getClass().getName(), "Token is Expired");
                }

            }
        };
    }

    public void LogoutButtonClicked(View view) {
        Log.e(getClass().getName(), "Logging out!");
        putUserToLoginFlowAgain();
    }

    private void signOutAccount() {
        LoginManager.getInstance().logOut();
        FirebaseUserManager.getFirebaseAuthInstance().signOut();
    }

    private void putUserToLoginFlowAgain() {
        signOutAccount();
       showSignInForm();
    }
    private void showSignInForm(){
        startActivity(new Intent(this, SignIn.class));
        finish();
    }





    @Override
    public void onBackPressed() {
        backButtonClicked();
    }

    @Override
    public void backButtonClicked() {
        new CustomBackButton(() -> {
            if (backButton.isDoubleTapped()) {
                toastMessageBackButton.hideToast();
                super.onBackPressed();
                return;
            }
            toastMessageBackButton.showToast();
            backButton.registerFirstClick();
        }).backButtonIsClicked();

    }
}