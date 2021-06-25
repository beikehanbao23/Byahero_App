package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.*;

import com.rejowan.cutetoast.CuteToast;

import FirebaseUserManager.FirebaseUserManager;
import InternetConnection.ConnectionManager;
import Logger.CustomToastMessage;
import MenuButtons.ButtonClicksTimeDelay;
import MenuButtons.CustomBackButton;
import ValidateUser.UserManager;
import static com.example.commutingapp.R.string.*;
import static com.example.commutingapp.R.id.*;
import static com.example.commutingapp.R.layout.*;

public class SignIn extends AppCompatActivity implements CustomBackButton {

    private EditText email, password;
    private Button facebookButton, googleButton, loginButton;
    private FirebaseUserManager firebaseUserManager;
    private TextView dontHaveAnAccountTextView, signUpTextView;
    private CustomToastMessage toastMessageUserAuthentication;
    private CustomToastMessage toastMessageNoInternetConnection;
    private CustomToastMessage toastMessageBackButton;
    private ButtonClicksTimeDelay backButtonClick;

    private ConnectionManager connectionManager;
    private ProgressBar circularProgressBar;
    private UserManager userManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_sign_in);
        email = findViewById(editlogin_TextEmail);
        password = findViewById(editLogin_TextPassword);
        loginButton = findViewById(LogInButton);
        facebookButton = findViewById(FacebookButton);
        googleButton = findViewById(GoogleButton);
        dontHaveAnAccountTextView = findViewById(TextView_DontHaveAnAccount);
        signUpTextView = findViewById(TextViewSignUp);
        circularProgressBar = findViewById(SignInProgressBar);


        backButtonClick = new ButtonClicksTimeDelay(2000);
        userManager = new UserManager();
        firebaseUserManager = new FirebaseUserManager();

        firebaseUserManager.initializeFirebase();


        toastMessageNoInternetConnection = new CustomToastMessage(this, getString(getNoInternetConnectionAtSignMessage), 2);
        toastMessageBackButton = new CustomToastMessage(this, getString(getDoubleTappedMessage), 10);

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

        userManager.verifyUserForSignIn(email, password);

        if (userManager.UserInputRequirementsFailedAtSignIn()) {
            return;
        }

        if (!connectionManager.PhoneHasInternetConnection()) {
            toastMessageNoInternetConnection.showToastWithLimitedTimeThenClose(2250);
            return;
        }

        AuthenticateUserToFirebase();
    }


    private void AuthenticateUserToFirebase() {

        String userUsername = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        startLoading();
        firebaseUserManager.getFirebaseInstance().signInWithEmailAndPassword(userUsername, userPassword).addOnCompleteListener(this, task -> {

            if (task.isSuccessful()) {
                finishLoading();
                firebaseUserManager.getCurrentUser();
                showMainScreen();
                return;
            }

            if (task.getException().getMessage() != null) {
                finishLoading();
                handleTaskExceptionResults(task);
            }


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


    private void handleTaskExceptionResults(Task<AuthResult> task) {
        try {
            throw task.getException();
        }catch(FirebaseNetworkException firebaseNetworkException) {
            toastMessageNoInternetConnection.showToastWithLimitedTimeThenClose(2250);
        }catch(FirebaseAuthInvalidUserException firebaseAuthInvalidUserException ){

        if(firebaseAuthInvalidUserException.getErrorCode().equals("ERROR_USER_DISABLED")){
            CuteToast.ct(this,getString(getDisabledAccountMessage),Toast.LENGTH_SHORT,3,true).show();
            return;
        }
            CuteToast.ct(this,getString(getIncorrectEmailOrPasswordMessage),Toast.LENGTH_SHORT,3,true).show();

        } catch (Exception e) {

        CuteToast.ct(this,getString(getSomethingWentWrongMessage),Toast.LENGTH_SHORT,3,true).show();
        Log.e("SignIn",e.getMessage().toUpperCase());

        }
    }

    private void showMainScreen() {
        startActivity(new Intent(this, MainScreen.class));
        finish();
    }

    @Override
    public void backButtonClicked() {

        CustomBackButton customBackButton = () -> {
            if (backButtonClick.isDoubleTapped()) {
                toastMessageBackButton.hideToast();
                super.onBackPressed();
                return;
            }
            toastMessageBackButton.showToast();
            backButtonClick.registerFirstClick();
        };
        customBackButton.backButtonClicked();
    }
}