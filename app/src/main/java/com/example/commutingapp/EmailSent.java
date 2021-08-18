package com.example.commutingapp;



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

import FirebaseUserManager.FirebaseManager;
import Logger.CustomDialogs;
import Logger.CustomToastMessage;
import MenuButtons.CustomBackButton;
import UI.ActivitySwitcher;
import UI.AttributesInitializer;
import UI.BindingDestroyer;

import static com.example.commutingapp.R.string.doubleTappedMessage;
import static com.example.commutingapp.R.string.resendEmailFailedMessage;
import static com.example.commutingapp.R.string.resendEmailSuccessMessage;

public class EmailSent extends AppCompatActivity implements BindingDestroyer, AttributesInitializer {

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
        initializeAttributes();
        FirebaseManager.initializeFirebaseApp();
        displayUserEmailToTextView();


    }

    @Override
    public void initializeAttributes() {
        emailDialogBinding = CustomEmailsentDialogBinding.inflate(getLayoutInflater());
        progressbarBinding = CircularProgressbarBinding.bind(emailDialogBinding.getRoot());
        setContentView(emailDialogBinding.getRoot());

        customPopupDialog = new CustomDialogs(this);
        toastMessageBackButton = new CustomToastMessage(this, getString(doubleTappedMessage), 10);
    }

    @Override
    public void destroyBinding() {
        emailDialogBinding = null;
        progressbarBinding = null;
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

            FirebaseManager.getFirebaseUserInstance().reload().addOnCompleteListener(emailReload -> {
                if (emailReload.isSuccessful() && FirebaseManager.getFirebaseUserInstance().isEmailVerified()) {
                    showMainScreenActivity();
                }
                if (emailReload.getException() != null) {
                    handleEmailVerificationException(emailReload);
                }
            });

    }

    //TODO refactor this
    private void handleEmailVerificationException(Task<?> task){

        try {
            throw Objects.requireNonNull(task.getException());
        }catch (FirebaseNetworkException firebaseNetworkException) {

            setThreadDisabled(true);
            showNoInternetActivity();
        }catch (Exception ignored){}
    }


    private void showNoInternetActivity(){
        progressbarBinding.circularProgressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(()-> {
           ActivitySwitcher.INSTANCE.startActivityOf(this,NoInternet.class);
           progressbarBinding.circularProgressBar.setVisibility(View.INVISIBLE);
       },DELAY_INTERVAL);
    }



    @Override
    public void onBackPressed() {
       new CustomBackButton(this,this).applyDoubleClickToExit();
    }



    private void displayUserEmailToTextView() {
        String userEmail = FirebaseManager.getFirebaseUserInstance().getEmail();
        if (userEmail != null) {
            emailDialogBinding.textViewEmail.setText(userEmail);
        }
    }





    public void resendEmailIsClicked(View view) {
        FirebaseManager.getFirebaseUserInstance().sendEmailVerification().addOnCompleteListener(task -> {
            startTimerForVerification();
            if (task.isSuccessful()) {
                customPopupDialog.showSuccessDialog("New email sent", getString(resendEmailSuccessMessage));
                return;
            }
            customPopupDialog.showWarningDialog("Please check your inbox", getString(resendEmailFailedMessage));
        });
    }

    private void displayWhenVerificationTimerStarted(long secondsLeft) {
        emailDialogBinding.ResendVerificationButton.setTextColor(ContextCompat.getColor(this, R.color.gray));
        emailDialogBinding.ResendVerificationButton.setText("Resend verification in " + secondsLeft + "s");
        emailDialogBinding.ResendVerificationButton.setEnabled(false);
    }

    private void displayWhenVerificationTimerIsFinished() {
        emailDialogBinding.ResendVerificationButton.setTextColor(ContextCompat.getColor(this, R.color.blue2));
        emailDialogBinding.ResendVerificationButton.setText("Resend verification");
        emailDialogBinding.ResendVerificationButton.setEnabled(true);
    }

    private void startTimerForVerification() {

        verificationTimer = new CountDownTimer(twoMinutes, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                displayWhenVerificationTimerStarted(secondsLeft);
            }

            @Override
            public void onFinish() {
                displayWhenVerificationTimerIsFinished();
            }
        }.start();

    }

    private void removeVerificationTimer() {
        if (verificationTimer != null) {
            verificationTimer.cancel();
        }

    }

    private void showMainScreenActivity() {

        ActivitySwitcher.INSTANCE.startActivityOf(this,this,MainScreen.class);
    }

    @Override
    protected void onDestroy() {
        removeVerificationTimer();
        setThreadDisabled(true);
        destroyBinding();
        super.onDestroy();
    }


}
