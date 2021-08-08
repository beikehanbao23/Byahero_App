package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.commutingapp.databinding.CustomEmailsentDialogBinding;

import java.util.Objects;

import FirebaseUserManager.FirebaseUserManager;
import Logger.CustomDialogs;
import Logger.CustomToastMessage;
import MenuButtons.BackButtonDoubleClicked;
import MenuButtons.CustomBackButton;

import static com.example.commutingapp.R.string.doubleTappedMessage;
import static com.example.commutingapp.R.string.resendEmailFailedMessage;
import static com.example.commutingapp.R.string.resendEmailSuccessMessage;

public class EmailSent extends AppCompatActivity implements BackButtonDoubleClicked {

    private final long twoMinutes = 120000;
    private final long threadInterval = 2000;
    private CustomDialogs customPopupDialog;

    private CountDownTimer verificationTimer;
    private CustomToastMessage toastMessageBackButton;
    private volatile boolean exitThread;
    private CustomEmailsentDialogBinding emailDialogBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        emailDialogBinding = CustomEmailsentDialogBinding.inflate(getLayoutInflater());
        setContentView(emailDialogBinding.getRoot());

        customPopupDialog = new CustomDialogs(this);
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
            while (!exitThread) {
                reloadUserEmailCurrentData();
                try {
                    Thread.sleep(threadInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void reloadUserEmailCurrentData() {
        if (!FirebaseUserManager.getFirebaseUserInstance().isEmailVerified()) {
            FirebaseUserManager.getFirebaseUserInstance().reload().addOnCompleteListener(emailReload -> {
                if (emailReload.isSuccessful() && FirebaseUserManager.getFirebaseUserInstance().isEmailVerified()) {
                    showMainScreen();
                }
                if (emailReload.getException() != null) {
                    String message = emailReload.getException().getMessage();
                    customPopupDialog.showErrorDialog("ERROR", Objects.requireNonNull(message));
                }
            });
        }
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
        emailDialogBinding.textViewEmail.setText(userEmail);
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
        emailDialogBinding.ResendVerificationButton.setTextColor(ContextCompat.getColor(this, R.color.gray));
        emailDialogBinding.ResendVerificationButton.setText("Resend verification in " + secondsLeft + "s");
        emailDialogBinding.ResendVerificationButton.setEnabled(false);
    }

    private void DisplayWhenVerificationTimerIsFinished() {
        emailDialogBinding.ResendVerificationButton.setTextColor(ContextCompat.getColor(this, R.color.blue2));
        emailDialogBinding.ResendVerificationButton.setText("Resend verification");
        emailDialogBinding.ResendVerificationButton.setEnabled(true);
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

    private void showMainScreen() {

        startActivity(new Intent(this, MainScreen.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        removeVerificationTimer();
        exitThread = true;
        super.onDestroy();
    }

}
