package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import FirebaseUserManager.FirebaseUserManager;
import InternetConnection.ConnectionManager;
import Logger.CustomToastMessage;
import Logger.LoggerErrorMessage;
import MenuButtons.Clicks_BackButton;

public class SignIn extends AppCompatActivity {
    private Clicks_BackButton backButton;
    private EditText email, password;
    private FirebaseUserManager firebaseUserManager;
    private CustomToastMessage toastMessageIncorrectUserNameAndPassword;
    private CustomToastMessage toastMessageNoInternetConnection;
    private ConnectionManager connectionManager;
    private ProgressBar circularProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        email = findViewById(R.id.editlogin_TextEmail);
        password = findViewById(R.id.editLogin_TextPassword);

        circularProgressBar = findViewById(R.id.SignInProgressBar);

        backButton = new Clicks_BackButton(this.getBaseContext(), "Tap again to exit");

        firebaseUserManager = new FirebaseUserManager();
        firebaseUserManager.initializeFirebase();

        toastMessageIncorrectUserNameAndPassword = new CustomToastMessage(this, "Username or password is incorrect", 3);
        toastMessageNoInternetConnection = new CustomToastMessage(this, LoggerErrorMessage.getNoInternetConnectionErrorMessage(), 2);

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
        backButton.showToastMessageThenBack();
    }

    public void SignInButtonIsClicked(View view) {
        String userUsername = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();


    firebaseUserManager.verifyUserForSignIn(email, password);

    if (firebaseUserManager.UserInputRequirementsFailedAtSignIn()) {
        return;
    }

    if (!connectionManager.PhoneHasInternetConnection()) {
        toastMessageNoInternetConnection.showMessage();
        return;
    }

    firebaseUserManager.getFirebaseAuthenticate().signInWithEmailAndPassword(userUsername, userPassword).addOnCompleteListener(this, task -> {
        if (task.isSuccessful()) {

            circularProgressBar.setVisibility(View.VISIBLE);
            firebaseUserManager.getCurrentUser();
            toastMessageIncorrectUserNameAndPassword.hideMessage();
            showMainScreen();
            return;
        }
        toastMessageIncorrectUserNameAndPassword.showMessage();
        toastMessageNoInternetConnection.hideMessage();
    });


    }

    private void showMainScreen() {
        startActivity(new Intent(this, MainScreen.class));
        finish();
    }

}