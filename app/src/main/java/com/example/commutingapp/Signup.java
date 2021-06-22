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
import ValidateUser.UserManager;


public class Signup extends AppCompatActivity {

    private EditText name, email, phoneNumber, password, confirmPassword;
    private FirebaseUserManager firebaseUserManager;
    private CustomToastMessage toastMessageErrorCreatingAccount;
    private CustomToastMessage toastMessageNoInternetConnection;
    private ConnectionManager connectionManager;
    private ProgressBar circularProgressbar;
    private UserManager userManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.editTextSignUpName);
        email = findViewById(R.id.editTextSignUpEmailAddress);
        phoneNumber = findViewById(R.id.editTextSignUpPhone);
        password = findViewById(R.id.editTextSignUpPassword);
        confirmPassword = findViewById(R.id.editSignUpConfirmPassword);

        circularProgressbar = findViewById(R.id.SignUpProgressBar);

        toastMessageErrorCreatingAccount = new CustomToastMessage(this, "Something went wrong with creating your account. Please try again later.", 3);
        toastMessageNoInternetConnection = new CustomToastMessage(this, LoggerErrorMessage.getNoInternetConnectionErrorMessage(), 2);
        userManager = new UserManager();
        firebaseUserManager = new FirebaseUserManager();
        firebaseUserManager.initializeFirebase();


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

    public void CreateBttnClicked(View view) {

        userManager.verifyUserForSignUp(name, email, phoneNumber, password, confirmPassword);
        if (userManager.UserInputRequirementsFailedAtSignUp()) {
            return;
        }

        if (!connectionManager.PhoneHasInternetConnection()) {
            toastMessageNoInternetConnection.showMessage();
            return;
        }


        toastMessageNoInternetConnection.hideMessage();
        circularProgressbar.setVisibility(View.VISIBLE);
        signUpToFirebase();


    }

    private void signUpToFirebase() {
        String userEmail = email.getText().toString().trim();
        String userConfirmPassword = confirmPassword.getText().toString().trim();
        firebaseUserManager.getFirebaseInstance().createUserWithEmailAndPassword(userEmail, userConfirmPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseUserManager.getCurrentUser();
                toastMessageErrorCreatingAccount.hideMessage();
                showMainScreen();
                return;
            }

            toastMessageErrorCreatingAccount.showMessage();

        });

    }

    public void showMainScreen() {
        startActivity(new Intent(this, MainScreen.class));
        finish();
    }

}