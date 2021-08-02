package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

import FirebaseUserManager.FirebaseUserManager;
import Logger.CustomDialogs;
import static com.example.commutingapp.R.id.*;
import static com.example.commutingapp.R.string.*;
import static com.example.commutingapp.R.layout.*;

public class EmailSent extends AppCompatActivity {

    private ProgressBar circularProgressBar;
    private CustomDialogs customPopupDialog;
    private Button resendVerificationButton;
    private CountDownTimer verificationTimer;
    private final long twoMinutes = 120000;
    private TextView emailDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(custom_emailsent_dialog);
        circularProgressBar = findViewById(LoadingProgressBar);
        customPopupDialog = new CustomDialogs(this);
        resendVerificationButton = findViewById(ResendVerificationButton);
        emailDisplay = findViewById(textViewEmail);



        displayUserEmail();
    }




    private void displayUserEmail(){
    String userEmail = FirebaseUserManager.getFirebaseUserInstance().getEmail();

    if(Objects.equals(userEmail, null)){
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
                handleTaskExceptionResults(emailReload);
            }
            circularProgressBar.setVisibility(View.INVISIBLE);
        });
    }
    private void handleTaskExceptionResults(Task<?> task) {
        try {
            throw task.getException();
        } catch (FirebaseNetworkException firebaseNetworkException) {
            showNoInternetDialog();
        } catch (FirebaseAuthInvalidUserException firebaseAuthInvalidUserException) {
            if (firebaseAuthInvalidUserException.getErrorCode().equals("ERROR_USER_DISABLED")) {
                customPopupDialog.showErrorDialog("Oops..", getString(disabledAccountMessage));
                return;
            }
            customPopupDialog.showErrorDialog("Oops..", getString(incorrectEmailOrPasswordMessage));
        } catch (Exception e) {
            customPopupDialog.showErrorDialog("Oops..", e.getMessage());
        }
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
    private void showNoInternetDialog() {

        startActivity(new Intent(this, NoInternet.class));
    }
    private void showMainScreen() {

        startActivity(new Intent(this,MainScreen.class));
        finish();
    }
    @Override
    protected void onDestroy() {
        removeVerificationTimer();
        super.onDestroy();
    }

    /*

    2. threads
    3. timer
    4. add functionality to two buttons
    5. add colors of Resend Verification button
    6. add Start and FinishLoading
    7. remove verification timer in onDestroy Method
     */
}
