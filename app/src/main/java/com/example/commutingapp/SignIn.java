package com.example.commutingapp;

import android.app.Dialog;
import android.content.Intent;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;

import android.view.View;
import android.view.WindowManager;
import android.widget.*;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.kinda.alert.KAlertDialog;
import com.rejowan.cutetoast.CuteToast;

import FirebaseUserManager.FirebaseUserManager;
import InternetConnection.ConnectionManager;
import Logger.CustomToastMessage;
import MenuButtons.BackButtonDoubleClicked;
import MenuButtons.CustomBackButton;
import MenuButtons.backButton;
import ValidateUser.UserManager;


import static com.example.commutingapp.R.id.*;
import static com.example.commutingapp.R.layout.*;
import static com.example.commutingapp.R.string.*;



public class SignIn extends AppCompatActivity implements BackButtonDoubleClicked {

    private EditText email, password;
    private Button facebookButton, googleButton, loginButton;
    private Dialog noInternetDialog;
    private TextView dontHaveAnAccountTextView,signUpTextView,resendEmailTextView;
    private CustomToastMessage toastMessageBackButton;
    private ConnectionManager connectionManager;
    private ProgressBar circularProgressBar,circularProgressBarEmailSent;
    private UserManager userManager;
    private CountDownTimer verificationTimer;
    private final long twoMinutes = 120000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(activity_sign_in);
        initializeAttributes();

        FirebaseUserManager.initializeFirebase();
        noInternetDialog.setContentView(custom_no_internet_dialog);



    }

    @Override
    protected void onDestroy() {
        if(noInternetDialog.isShowing()){
            noInternetDialog.dismiss();
        }

        if(verificationTimer != null){
            verificationTimer.cancel();
        }
        super.onDestroy();
    }

    public void GoToSettingsClicked(View view) {
        startActivity(new Intent(Settings.ACTION_SETTINGS));
    }


    public void SignUpTextClicked(View view) {
        this.startActivity(new Intent(this, Signup.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectionManager = new ConnectionManager(this);
    }


    public void retryButtonClicked(View view) {
        if (connectionManager.PhoneHasInternetConnection() && noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }

    }


    @Override
    public void onBackPressed() {
        backButtonClicked();
    }


    public void SignInButtonIsClicked(View view) {

        userManager = new UserManager(getBaseContext(), email, password);
        if (userManager.UserInputRequirementsFailedAtSignIn()) {
            return;
        }

        if (!connectionManager.PhoneHasInternetConnection()) {
            showNoInternetDialog();
            return;
        }

        ProceedToLogin();
    }
    //TODO retest exceptions


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


    public void refreshButtonClicked(View view) {

        circularProgressBarEmailSent = findViewById(LoadingProgressBar);
        circularProgressBarEmailSent.setVisibility(View.VISIBLE);
        FirebaseUserManager.getFirebaseUser().reload().addOnCompleteListener(task -> {
            if (task.isSuccessful() && FirebaseUserManager.getFirebaseUser().isEmailVerified()) {
                showMainScreen();
            }
            if(task.getException() != null){
                handleTaskExceptionResults(task);
            }
            circularProgressBarEmailSent.setVisibility(View.INVISIBLE);
        });
    }

    //TODO
    public void resendEmailIsClicked(View view) {

        FirebaseUserManager.getFirebaseUser().sendEmailVerification().addOnCompleteListener(task -> {
            startTimerForVerification();
            if (task.isSuccessful()) {
                CuteToast.ct(this, getString(getResendEmailSuccessMessage), Toast.LENGTH_SHORT, 4, true).show();
                return;
            }
            CuteToast.ct(this, getString(getResendEmailFailedMessage), Toast.LENGTH_SHORT, 2, true).show();

        });
    }

















    private void setDisplayForResendEmailTextViewWhile_TimerOnTick(long secondsLeft){
        resendEmailTextView.setTextColor(ContextCompat.getColor(this,R.color.gray));
        resendEmailTextView.setText("Resend verification in "+ secondsLeft+"s");
        resendEmailTextView.setEnabled(false);
    }

    private void setDisplayForResendEmailTextWhenTimerFinished(){
        resendEmailTextView.setTextColor(ContextCompat.getColor(this,R.color.blue2));
        resendEmailTextView.setText("Resend verification");
        resendEmailTextView.setEnabled(true);
    }


    private void startTimerForVerification(){
        resendEmailTextView = findViewById(textViewResendEmail);
        verificationTimer = new CountDownTimer(twoMinutes, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished/1000;
                setDisplayForResendEmailTextViewWhile_TimerOnTick(secondsLeft);
            }

            @Override
            public void onFinish() {
                setDisplayForResendEmailTextWhenTimerFinished();
            }
        }.start();

    }



    private void ProceedToLogin() {

        String userUsername = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        startLoading();
        FirebaseUserManager.getFirebaseAuth().signInWithEmailAndPassword(userUsername, userPassword).addOnCompleteListener(this, signInTask -> {
            if (signInTask.isSuccessful()) {
                FirebaseUserManager.getCurrentUser();
                VerifyUserEmail();
                return;
            }
            if (signInTask.getException() != null) {
                finishLoading();
                handleTaskExceptionResults(signInTask);
            }
        });

    }


    private void VerifyUserEmail() {
        FirebaseUserManager.getFirebaseUser().reload().addOnCompleteListener(task -> {

            if (task.isSuccessful() && FirebaseUserManager.getFirebaseUser().isEmailVerified()) {
                showMainScreen();
                return;
            }
            showEmailSentDialog();


        });
    }

    private void startLoading() {
        circularProgressBar.setVisibility(View.VISIBLE);
        email.setEnabled(false);
        password.setEnabled(false);
        loginButton.setEnabled(false);
        facebookButton.setEnabled(false);
        googleButton.setEnabled(false);
        dontHaveAnAccountTextView.setEnabled(false);
        signUpTextView.setEnabled(false);
    }

    private void finishLoading() {
        circularProgressBar.setVisibility(View.INVISIBLE);
        email.setEnabled(true);
        password.setEnabled(true);
        loginButton.setEnabled(true);
        facebookButton.setEnabled(true);
        googleButton.setEnabled(true);
        dontHaveAnAccountTextView.setEnabled(true);
        signUpTextView.setEnabled(true);
    }


    private void handleTaskExceptionResults(Task<?> task) {
        try {
            throw task.getException();
        } catch (FirebaseNetworkException firebaseNetworkException) {
            showNoInternetDialog();
        } catch (FirebaseAuthInvalidUserException firebaseAuthInvalidUserException) {
            if (firebaseAuthInvalidUserException.getErrorCode().equals("ERROR_USER_DISABLED")) {
                showErrorDialog("Oops..",getString(getDisabledAccountMessage));
                return;
            }
            showErrorDialog("Oops..",getString(getIncorrectEmailOrPasswordMessage));
        } catch (Exception e) {
            showErrorDialog("Oops..",e.getMessage());
        }
    }

    private void initializeAttributes() {
        email = findViewById(editlogin_TextEmail);
        password = findViewById(editLogin_TextPassword);
        loginButton = findViewById(LogInButton);
        facebookButton = findViewById(FacebookButton);
        googleButton = findViewById(GoogleButton);
        dontHaveAnAccountTextView = findViewById(TextView_DontHaveAnAccount);
        signUpTextView = findViewById(TextViewSignUp);
        circularProgressBar = findViewById(LoadingProgressBar);

        toastMessageBackButton = new CustomToastMessage(this, getString(getDoubleTappedMessage), 10);
        noInternetDialog = new Dialog(this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);



    }

    private void showMainScreen() {
        startActivity(new Intent(this, MainScreen.class));
        finish();
    }

    private void showNoInternetDialog() {
        if (noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
            return;
        }
        noInternetDialog.show();

    }

    private void showEmailSentDialog() {

     setContentView(custom_emailsent_dialog);
        displayUsersEmailToTextView();

    }

    private void displayUsersEmailToTextView() {
        TextView emailTextView = findViewById(textViewEmail);
        String usersEmail = FirebaseUserManager.getFirebaseUser().getEmail();
        emailTextView.setText(usersEmail);
    }
    private void showErrorDialog(String title,String contextText){
        KAlertDialog pDialog = new KAlertDialog(this, KAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

    }
}