package com.example.commutingapp.views.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.commutingapp.databinding.ActivitySplashscreenBinding;
import com.example.commutingapp.utils.FirebaseUserManager.FirebaseManager;
import com.example.commutingapp.utils.ui_utilities.ActivitySwitcher;
import com.example.commutingapp.utils.ui_utilities.AttributesInitializer;
import com.example.commutingapp.utils.ui_utilities.BindingDestroyer;
import com.example.commutingapp.utils.ui_utilities.ScreenDimension;
import com.example.commutingapp.viewmodels.SplashScreenViewModel;
import com.example.commutingapp.views.MenuButtons.CustomBackButton;

public class splashscreen extends AppCompatActivity implements BindingDestroyer, AttributesInitializer {

    private final int delayInMillis = 1250;
    private SplashScreenViewModel viewModel;
    private ActivitySplashscreenBinding activitySplashscreenBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeAttributes();

        FirebaseManager.initializeFirebaseApp();
        FirebaseManager.getCreatedUserAccount();

        viewModel = new ViewModelProvider(this).get(SplashScreenViewModel.class);

    }

    @Override
    public void initializeAttributes() {
        new ScreenDimension(getWindow()).setWindowToFullScreen();
        activitySplashscreenBinding = ActivitySplashscreenBinding.inflate(getLayoutInflater());
        setContentView(activitySplashscreenBinding.getRoot());

    }

    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(this::startTransitionToNextActivity, delayInMillis);

    }


    private void startTransitionToNextActivity() {
        viewModel.setUserSignInProvider();

        viewModel.onNavigateToUserDetailsSuccess().observe(this, transition -> {
            if (transition.getContentIfNotHandled() != null) {
                showMainScreenActivity();
            }
        });

      if(viewModel.onNavigateToUserDetailsSuccess().getValue() == null){
          showIntroSlidersActivity();
      }

    }



    @Override
    public void onBackPressed() {
        new CustomBackButton(this, this).applyDoubleClickToExit();
    }
    private void showMainScreenActivity() {
        ActivitySwitcher.INSTANCE.startActivityOf(this,this, MainScreen.class);
    }
    private void showIntroSlidersActivity() {
        ActivitySwitcher.INSTANCE.startActivityOf(this, this, IntroSlider.class);
    }
    @Override protected void onDestroy() {
        destroyBinding();
        super.onDestroy();
    }
    @Override public void destroyBinding() {
        activitySplashscreenBinding = null;
    }

}



         