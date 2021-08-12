package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.commutingapp.databinding.CircularProgressbarBinding;
import com.example.commutingapp.databinding.CustomEmailsentDialogBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;

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
    private final long DELAY_INTERVAL = 1500;
    private CountDownTimer verificationTimer;
    private CustomToastMessage toastMessageBackButton;
    private volatile boolean exitThread;
    private CustomEmailsentDialogBinding emailDialogBinding;
    private CircularProgressbarBinding progressbarBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        emailDialogBinding = CustomEmailsentDialogBinding.inflate(getLayoutInflater());
        progressbarBinding = CircularProgressbarBinding.bind(emailDialogBinding.getRoot());
        setContentView(emailDialogBinding.getRoot());

        customPopupDialog = new CustomDialogs(this);
        toastMessageBackButton = new CustomToastMessage(this, getString(doubleTappedMessage), 10);
        FirebaseUserManager.initializeFirebase();
        displayUserEmailToTextView();


    }


    private void refreshEmailAutomatically() throws Exception {
        new Thread(() -> {
            while (!exitThread) {
                reloadUserEmail();
                Log.e("THREAD STATUS: ","RUNNING");
                try { Thread.sleep(threadInterval); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }).start();


    }


    @Override
    protected void onStart() {
        super.onStart();

            setThreadDisabled(false);
            try { refreshEmailAutomatically(); } catch (Exception exception) { exception.printStackTrace(); }
    }





    private void setThreadDisabled(Boolean threadState){
     exitThread = threadState;
    }

    private void reloadUserEmail() {

            FirebaseUserManager.getFirebaseUserInstance().reload().addOnCompleteListener(emailReload -> {
                if (emailReload.isSuccessful() && FirebaseUserManager.getFirebaseUserInstance().isEmailVerified()) {
                    showMainScreenActivity();
                }
                if (emailReload.getException() != null) {
                    handleEmailVerificationException(emailReload);
                }
            });

    }
    private void handleEmailVerificationException(Task<?> task){

        try {
            throw Objects.requireNonNull(task.getException());
        }catch (FirebaseNetworkException firebaseNetworkException) {
            progressbarBinding.circularProgressBar.setVisibility(View.VISIBLE);
            setThreadDisabled(true);
            showNoInternetActivity();
        }catch (Exception ignored){}
    }


    private void showNoInternetActivity(){
        new Handler().postDelayed(()-> {
           startActivity(new Intent(this,NoInternet.class));
           progressbarBinding.circularProgressBar.setVisibility(View.INVISIBLE);
       },DELAY_INTERVAL);
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
        if (userEmail != null) {
            emailDialogBinding.textViewEmail.setText(userEmail);
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

    private void DisplayWhenVerificationTimerStarted(long secondsLeft) {
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
                DisplayWhenVerificationTimerStarted(secondsLeft);
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

    private void showMainScreenActivity() {

        startActivity(new Intent(this, MainScreen.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        removeVerificationTimer();
        setThreadDisabled(true);
        super.onDestroy();
    }

}
