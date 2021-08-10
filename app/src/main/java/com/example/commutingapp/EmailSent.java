package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

    private CountDownTimer verificationTimer;
    private CustomToastMessage toastMessageBackButton;
    private volatile boolean exitThread;
    private CustomEmailsentDialogBinding emailDialogBinding;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        emailDialogBinding = CustomEmailsentDialogBinding.inflate(getLayoutInflater());
        setContentView(emailDialogBinding.getRoot());

        customPopupDialog = new CustomDialogs(this);
        toastMessageBackButton = new CustomToastMessage(this, getString(doubleTappedMessage), 10);
        FirebaseUserManager.initializeFirebase();
        displayUserEmailToTextView();


    }


    private void refreshEmailAutomatically() throws Exception {
        thread = new Thread(() -> {
            while (!exitThread) {
                reloadUserEmailCurrentData();
                Log.e("THREAD STATUS","RUNNING");
                try {
                    Thread.sleep(threadInterval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    @Override
    protected void onStart() {
        super.onStart();

            Log.e("THREAD IS ","NULL");
            setThreadDisabled(false);
            try {
                refreshEmailAutomatically();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

    }

    @Override
    protected void onStop() {
        super.onStop();
        setThreadDisabled(true);
    }


    private void setThreadDisabled(Boolean threadState){
     exitThread = threadState;
    }
    private void reloadUserEmailCurrentData() {

            FirebaseUserManager.getFirebaseUserInstance().reload().addOnCompleteListener(emailReload -> {
                if (emailReload.isSuccessful() && FirebaseUserManager.getFirebaseUserInstance().isEmailVerified()) {
                    showMainScreenActivity();
                }
                if (emailReload.getException() != null) {
                    handleException(emailReload);

                }
            });

    }
    private void handleException(Task<?> task){
        try {
            throw Objects.requireNonNull(task.getException());
        } catch (FirebaseNetworkException firebaseNetworkException) {
            showNoInternetActivity();
        } catch (Exception ex) {
            customPopupDialog.showErrorDialog("Error", Objects.requireNonNull(task.getException().getMessage()));
        }

    }
    private void showNoInternetActivity(){

        startActivity(new Intent(this,NoInternet.class));
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
