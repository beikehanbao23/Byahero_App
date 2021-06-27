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
import ValidateUser.UserManager;
import static com.example.commutingapp.R.string.*;
import static com.example.commutingapp.R.id.*;
import static com.example.commutingapp.R.layout.*;

public class Signup extends AppCompatActivity {

    private EditText email, password, confirmPassword;

    private CustomToastMessage toastMessageErrorCreatingAccount;
    private CustomToastMessage toastMessageNoInternetConnection;
    private ConnectionManager connectionManager;
    private ProgressBar circularProgressbar;
    private UserManager userManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_signup);


        email = findViewById(editTextSignUpEmailAddress);
        password = findViewById(editTextSignUpPassword);
        confirmPassword = findViewById(editSignUpConfirmPassword);

        circularProgressbar = findViewById(SignUpProgressBar);

        toastMessageErrorCreatingAccount = new CustomToastMessage(this, getString(getSomethingWentWrongMessage), 3);
        toastMessageNoInternetConnection = new CustomToastMessage(this, getString(getNoInternetConnectionAtSignMessage), 2);


        FirebaseUserManager.initializeFirebase();


    }


    public void backToSignInButton(View view) {
        startActivity(new Intent(this, SignIn.class));
        finish();

    }

    @Override
    public void onBackPressed() {
        backToSignInButton(null);
    }


    @Override
    protected void onStart() {
        super.onStart();
        connectionManager = new ConnectionManager(this);
    }

    public void CreateButtonClicked(View view) {
        userManager = new UserManager(getBaseContext(), email, password, confirmPassword);
        if (userManager.UserInputRequirementsFailedAtSignUp()) {
            return;
        }

        if (!connectionManager.PhoneHasInternetConnection()) {
            toastMessageNoInternetConnection.showToastWithLimitedTimeThenClose(2250);
            return;
        }



        SignUpUserToFirebase();



    }


    private void SignUpUserToFirebase(){
        String userEmail = email.getText().toString().trim();
        String userConfirmPassword = confirmPassword.getText().toString().trim();
        FirebaseUserManager.getFirebaseAuth().createUserWithEmailAndPassword(userEmail, userConfirmPassword).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                circularProgressbar.setVisibility(View.VISIBLE);
                FirebaseUserManager.getCurrentUser();
                toastMessageErrorCreatingAccount.hideToast();
                showMainScreen();
                return;
            }

            toastMessageErrorCreatingAccount.showToast();
        });
    }

/*
if task is successful then
send email verification
show message about 'please verify email'

if email is verified then
go to main screen

 */

    private void showMainScreen() {

        startActivity(new Intent(this, MainScreen.class));
        finish();
    }

}