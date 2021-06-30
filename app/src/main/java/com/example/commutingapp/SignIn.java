package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

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

    private TextView dontHaveAnAccountTextView, signUpTextView;
  //  private CustomToastMessage toastMessageNoInternetConnection;
    private CustomToastMessage toastMessageBackButton;


    private ConnectionManager connectionManager;
    private ProgressBar circularProgressBar;
    private UserManager userManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(activity_sign_in);
        initializeAttributes();



        FirebaseUserManager.initializeFirebase();


        //toastMessageNoInternetConnection = new CustomToastMessage(this, getString(getNoInternetConnectionAtSignMessage), 2);
        toastMessageBackButton = new CustomToastMessage(this, getString(getDoubleTappedMessage), 10);

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

    @Override
    public void onBackPressed() {
        backButtonClicked();
    }

    public void SignInButtonIsClicked(View view) {

        userManager = new UserManager(getBaseContext(), email, password);

        if (userManager.UserInputRequirementsFailedAtSignIn()) {
            return;
        }
    //TODO CREATE ANIMATION
        if (!connectionManager.PhoneHasInternetConnection()) {
           // toastMessageNoInternetConnection.showToastWithLimitedTimeThenClose(2250);

            return;
        }
        //TODO change variable name
        LoginUser();
    }


    private void LoginUser() {

        String userUsername = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        startLoading();
        FirebaseUserManager.getFirebaseAuth().signInWithEmailAndPassword(userUsername, userPassword).addOnCompleteListener(this, signInTask -> {

            if (signInTask.isSuccessful()) {
                FirebaseUserManager.getCurrentUser();
                LoginAndVerifyUser();
                return;
            }
            if (signInTask.getException() != null) {
                finishLoading();
                handleTaskExceptionResults(signInTask);
            }
        });

    }
    private void LoginAndVerifyUser(){
        FirebaseUserManager.getFirebaseUser().reload().addOnCompleteListener(reloadTask -> {
            if(reloadTask.isSuccessful() && FirebaseUserManager.getFirebaseUser().isEmailVerified()){
                showMainScreen();
                return;
            }
            //TODO set a toast message
            Log.e(getClass().getName(),"Email already sent please verify");
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


    //TODO retest exceptions
    private void handleTaskExceptionResults(Task<AuthResult> task) {
        try {

            throw task.getException();

        } catch (FirebaseNetworkException firebaseNetworkException) {
          //  toastMessageNoInternetConnection.showToastWithLimitedTimeThenClose(2250);
        } catch (FirebaseAuthInvalidUserException firebaseAuthInvalidUserException) {

            if (firebaseAuthInvalidUserException.getErrorCode().equals("ERROR_USER_DISABLED")) {
                CuteToast.ct(this, getString(getDisabledAccountMessage), Toast.LENGTH_LONG, 3, true).show();
                return;
            }
            CuteToast.ct(this, getString(getIncorrectEmailOrPasswordMessage), Toast.LENGTH_SHORT, 3, true).show();

        } catch (Exception e) {
            CuteToast.ct(this, e.getMessage(), Toast.LENGTH_SHORT, 3, true).show();
            Log.e("SignIn", e.getMessage().toUpperCase());
        }
    }

    private void showMainScreen() {
        startActivity(new Intent(this, MainScreen.class));
        finish();
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