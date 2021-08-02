package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Objects;

import FirebaseUserManager.FirebaseUserManager;
import Logger.CustomDialogs;
import Logger.CustomToastMessage;
import MenuButtons.BackButtonDoubleClicked;
import MenuButtons.CustomBackButton;

import static com.example.commutingapp.R.id.LoadingProgressBar;
import static com.example.commutingapp.R.id.ResendVerificationButton;
import static com.example.commutingapp.R.id.tag_accessibility_heading;
import static com.example.commutingapp.R.id.textViewEmail;
import static com.example.commutingapp.R.layout.custom_emailsent_dialog;
import static com.example.commutingapp.R.string.doubleTappedMessage;
import static com.example.commutingapp.R.string.resendEmailFailedMessage;
import static com.example.commutingapp.R.string.resendEmailSuccessMessage;

public class EmailSent extends AppCompatActivity implements BackButtonDoubleClicked {

    private final long twoMinutes = 120000;
    private ProgressBar circularProgressBar;
    private CustomDialogs customPopupDialog;
    private Button resendVerificationButton;
    private CountDownTimer verificationTimer;
    private TextView emailDisplay;
    private CustomToastMessage toastMessageBackButton;

    private boolean exitThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(custom_emailsent_dialog);
        circularProgressBar = findViewById(LoadingProgressBar);
        customPopupDialog = new CustomDialogs(this);
        resendVerificationButton = findViewById(ResendVerificationButton);
        emailDisplay = findViewById(textViewEmail);
        toastMessageBackButton = new CustomToastMessage(this, getString(doubleTappedMessage), 10);
        FirebaseUserManager.initializeFirebase();
        exitThread = false;
        displayUserEmailToTextView();

        try {
            refreshEmailAutomatically();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }



    private void refreshEmailAutomatically() throws Exception {
        new Thread(() -> {
             while(!exitThread) {
                     Log.e("Thread status: ", "ACTIVE|RUNNING");
                     if(!FirebaseUserManager.getFirebaseUserInstance().isEmailVerified()){
                         FirebaseUserManager.getFirebaseUserInstance().reload().addOnCompleteListener(emailReload -> {
                             if (emailReload.isSuccessful() && FirebaseUserManager.getFirebaseUserInstance().isEmailVerified()) {
                                 showMainScreen();
                             }
                             if (emailReload.getException() != null) {
                                 String message = emailReload.getException().getMessage();
                                 customPopupDialog.showErrorDialog("ERROR", Objects.requireNonNull(message));
                             }
                             Log.e("Email Status: ", "NOT VERIFIED");
                         });

                     }
                     try {
                         Thread.sleep(2000);
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
             }

        }).start();






    }

    @Override
    public void onBackPressed() {
        backButtonClicked();
    }

    @Override
    public void backButtonClicked() {

        new CustomBackButton(() -> {
            if (MenuButtons.backButton.isDoubleTapped()) {
                toastMessageBackButton.hideToast();
                super.onBackPressed();
                return;
            }
            toastMessageBackButton.showToast();
            MenuButtons.backButton.registerFirstClick();
        }).backButtonIsClicked();
    }

    private void displayUserEmailToTextView() {
        String userEmail = FirebaseUserManager.getFirebaseUserInstance().getEmail();

        if (Objects.equals(userEmail, null)) {
            throw new RuntimeException("Email is Null");
        }
        emailDisplay.setText(userEmail);
    }

    public void refreshButtonClicked(View view) {
        circularProgressBar.setVisibility(View.VISIBLE);
        FirebaseUserManager.getFirebaseUserInstance().reload().addOnCompleteListener(emailReload -> {
            if (emailReload.isSuccessful() && FirebaseUserManager.getFirebaseUserInstance().isEmailVerified()) {
                showMainScreen();
            }
            if (emailReload.getException() != null) {
                String message = emailReload.getException().getMessage();
                customPopupDialog.showErrorDialog("ERROR", Objects.requireNonNull(message));
            }
            circularProgressBar.setVisibility(View.INVISIBLE);
        });
    }

    public void resendEmailIsClicked(View view) {
        FirebaseUserManager.getFirebaseUserInstance().sendEmailVerification().addOnCompleteListener(task -> {
            startTimerForVerification();
            if (task.isSuccessful()) {
                customPopupDialog.showSuccessDialog("New email sent", getString(resendEmailSuccessMessage));
                return;
            }
            customPopupDialog.showWarningDialog("Please check your inbox", getString(resendEmailFailedMessage));
        });
    }

    private void DisplayWhenVerificationTimerStarts(long secondsLeft) {
        resendVerificationButton.setTextColor(ContextCompat.getColor(this, R.color.gray));
        resendVerificationButton.setText("Resend verification in " + secondsLeft + "s");
        resendVerificationButton.setEnabled(false);
    }

    private void DisplayWhenVerificationTimerIsFinished() {
        resendVerificationButton.setTextColor(ContextCompat.getColor(this, R.color.blue2));
        resendVerificationButton.setText("Resend verification");
        resendVerificationButton.setEnabled(true);
    }

    private void startTimerForVerification() {

        verificationTimer = new CountDownTimer(twoMinutes, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                DisplayWhenVerificationTimerStarts(secondsLeft);
            }

            @Override
            public void onFinish() {
                DisplayWhenVerificationTimerIsFinished();
            }
        }.start();

    }

    private void removeVerificationTimer() {
        if (verificationTimer != null) {
            verificationTimer.cancel();
        }

    }
    //TODO Catch exception internet failure

    private void showMainScreen() {

        startActivity(new Intent(this, MainScreen.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        removeVerificationTimer();
        exitThread = true;
        Log.e("Status:","Calling onDestroy()");
        super.onDestroy();
    }

    /*
    add threads
     */
}
