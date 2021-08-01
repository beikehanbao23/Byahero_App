package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.UserInfo;

import java.util.ArrayList;
import java.util.List;

import FirebaseUserManager.FirebaseUserManager;
import Logger.CustomToastMessage;
import MenuButtons.BackButtonDoubleClicked;
import MenuButtons.CustomBackButton;
import MenuButtons.backButton;
import Screen.ScreenDimension;

public class MainScreen extends AppCompatActivity implements BackButtonDoubleClicked {

    private CustomToastMessage toastMessageBackButton;
    private TextView nameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ScreenDimension(getWindow()).windowToFullScreen();
        setContentView(R.layout.activity_main_screen);
        toastMessageBackButton = new CustomToastMessage(this, getString(R.string.doubleTappedMessage), 10);
        nameTextView = findViewById(R.id.nameTextView);


        FirebaseUserManager.initializeFirebase();
        checkFacebookTokenIfExpired();
       displayName();

    }


    private String getName() {
        for (UserInfo userInfo : FirebaseUserManager.getFirebaseUserInstance().getProviderData()) {
            if (userInfo.equals("facebook.com")) {
                return FirebaseUserManager.getFirebaseUserInstance().getDisplayName();
            }
        }
        return getFilteredEmail(FirebaseUserManager.getFirebaseUserInstance().getEmail());
    }
    private void displayName() {
      nameTextView.setText(getName());
      Log.e("Result from getName ",getName());

    }
    private String getFilteredEmail(String userEmail) {

        List<String> emailLists = getEmailExtensions();
        if (userEmail != null) {
            for (int counter = 0; counter < emailLists.size(); ++counter) {
                String emailExtension = emailLists.get(counter);
                if (userEmail.contains(emailExtension)) {
                    return userEmail.replaceAll(emailExtension, "");
                }
            }
        }

    return userEmail;
    }
    private List<String> getEmailExtensions() {
        List<String> list = new ArrayList<>();
        list.add("@gmail.com");
        list.add("@protonmail.ch");
        list.add("@yahoo.com");
        list.add("@hotmail.com");
        list.add("@outlook.com");
        return list;
    }
    private void checkFacebookTokenIfExpired() {
        new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    showExpiredTokenDialog();
                    Log.e(getClass().getName(), "Token is Expired");
                }

            }
        };
    }
    public void LogoutButtonClicked(View view) {
        Log.e(getClass().getName(), "Logging out!");
        putUserToLoginFlow();
    }
    private void signOutAccount() {
        LoginManager.getInstance().logOut();
        FirebaseUserManager.getFirebaseAuthInstance().signOut();
    }
    private void putUserToLoginFlow() {
        signOutAccount();
        showSignInForm();
    }
    private void showSignInForm() {
        startActivity(new Intent(this, SignIn.class));
        finish();
    }
    private void showExpiredTokenDialog(){
        startActivity(new Intent(this,TokenExpired.class));
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