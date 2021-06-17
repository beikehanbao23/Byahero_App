package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import FirebaseUserManager.FirebaseUserManager;
import InternetConnection.ConnectionManager;
import Logger.CustomToastMessage;
import Logger.LoggerErrorMessage;
import MenuButtons.Clicks_BackButton;

public class SignIn extends AppCompatActivity {
    private Clicks_BackButton backButton;
    private EditText username, password;
    private FirebaseUserManager firebaseUserManager;
    private CustomToastMessage ToastMessageIncorrectUserNameAndPassword;
    private CustomToastMessage ToastMessageNoInternetConnection;
    private ConnectionManager connectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        username = findViewById(R.id.editlogin_TextName);
        password = findViewById(R.id.editlogin_TextPassword);

        backButton = new Clicks_BackButton(this.getBaseContext(), 2000, "Tap again to exit");

        firebaseUserManager = new FirebaseUserManager();
        firebaseUserManager.initializeFirebase();

        ToastMessageIncorrectUserNameAndPassword = new CustomToastMessage(this, "Username or password is incorrect", 3);
        ToastMessageNoInternetConnection = new CustomToastMessage(this, LoggerErrorMessage.getNoInternetConnectionErrorMessage(), 2);

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
        String userEmail = username.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        firebaseUserManager.verifyUserForSignIn(username, password);

        if (firebaseUserManager.UserInputRequirementsFailedAtSignIn()) {
            return;
        }

        if (!connectionManager.PhoneHasInternetConnection()) {
            ToastMessageNoInternetConnection.showMessage();
            return;
        }

        firebaseUserManager.getFirebaseAuthenticate().signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                firebaseUserManager.getCurrentUser();
                ToastMessageIncorrectUserNameAndPassword.hideMessage();
                showMainScreen();
                return;
            }
            ToastMessageIncorrectUserNameAndPassword.showMessage();
            ToastMessageNoInternetConnection.hideMessage();
        });


    }

    private void showMainScreen() {
        startActivity(new Intent(this, MainScreen.class));
        finish();
    }

}