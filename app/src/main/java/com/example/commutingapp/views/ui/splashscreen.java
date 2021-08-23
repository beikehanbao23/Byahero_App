package com.example.commutingapp.views.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.commutingapp.databinding.ActivitySplashscreenBinding;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import com.example.commutingapp.utils.FirebaseUserManager.*;
import com.example.commutingapp.views.MenuButtons.CustomBackButton;
import com.example.commutingapp.utils.ui_utilities.ActivitySwitcher;
import com.example.commutingapp.utils.ui_utilities.AttributesInitializer;
import com.example.commutingapp.utils.ui_utilities.BindingDestroyer;
import com.example.commutingapp.utils.ui_utilities.ScreenDimension;

public class splashscreen extends AppCompatActivity implements BindingDestroyer, AttributesInitializer {

    private final int delayInMillis = 1000;
    private ActivitySplashscreenBinding activitySplashscreenBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeAttributes();

        FirebaseManager.initializeFirebaseApp();
        FirebaseManager.getCreatedUserAccount();
    }

    @Override public void initializeAttributes() {
        new ScreenDimension(getWindow()).setWindowToFullScreen();
        activitySplashscreenBinding = ActivitySplashscreenBinding.inflate(getLayoutInflater());
        setContentView(activitySplashscreenBinding.getRoot());
    }

    @Override protected void onStart() {
        super.onStart();
        new Handler().postDelayed(this::transitionToNextActivity, delayInMillis);

    }



    private void transitionToNextActivity(){
        if (FirebaseManager.hasAccountSignedIn()) {
            if (signInSuccess()) {
                showMainScreenActivity();
                return;
            }
        }
        showIntroSlidersActivity();

    }

    private boolean signInSuccess(){
        return FirebaseManager.getFirebaseUserInstance().isEmailVerified() ||
                isUserSignInUsingFacebook() ||
                isUserSignInUsingGoogle();
    }

    private boolean isUserSignInUsingFacebook(){
        return getProviderIdResult(FacebookAuthProvider.PROVIDER_ID);
    }

    private boolean isUserSignInUsingGoogle(){
        return getProviderIdResult(GoogleAuthProvider.PROVIDER_ID);
    }

    private boolean getProviderIdResult(String id){
        for (UserInfo ui : FirebaseManager.getFirebaseUserInstance().getProviderData()) {
            Log.e("Result",ui.getProviderId());
            if (ui.getProviderId().equals(id)) {
                return true; // return ui.getProviderId().equals(id) does not work here, always returning 'firebase' as providerId
            }
        }
        return false;
    }


    @Override public void onBackPressed() {
        new CustomBackButton(this,this).applyDoubleClickToExit();
    }
    private void showMainScreenActivity() {

        ActivitySwitcher.INSTANCE.startActivityOf(this,this, MainScreen.class);
    }
    private void showIntroSlidersActivity() {
        ActivitySwitcher.INSTANCE.startActivityOf(this,this, IntroSlider.class);
    }
    @Override protected void onDestroy() {
        destroyBinding();
        super.onDestroy();
    }
    @Override public void destroyBinding(){
        activitySplashscreenBinding = null;
    }


}



         