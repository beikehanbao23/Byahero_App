package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import FirebaseUserManager.FirebaseUserManager;
import InternetConnection.ConnectionManager;
import Logger.CustomToastMessage;
import ValidateUser.UserManager;

import static com.example.commutingapp.R.id.BackButton;
import static com.example.commutingapp.R.id.CreateButton;
import static com.example.commutingapp.R.id.SignUpProgressBar;
import static com.example.commutingapp.R.id.TextView_AlreadyHaveAccount;
import static com.example.commutingapp.R.id.TextView_LoginHere;
import static com.example.commutingapp.R.id.editSignUpConfirmPassword;
import static com.example.commutingapp.R.id.editTextSignUpEmailAddress;
import static com.example.commutingapp.R.id.editTextSignUpPassword;
import static com.example.commutingapp.R.layout.activity_signup;
import static com.example.commutingapp.R.string.getNoInternetConnectionAtSignMessage;
import static com.example.commutingapp.R.string.getSomethingWentWrongMessage;

public class Signup extends AppCompatActivity {

    private EditText email, password, confirmPassword;
    private Button backButton, createButton;
    private TextView alreadyHaveAnAccount, loginHere;
    private CustomToastMessage toastMessageErrorCreatingAccount;
    private CustomToastMessage toastMessageNoInternetConnection;
    private ConnectionManager connectionManager;
    private ProgressBar circularProgressbar;
    private UserManager userManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_signup);

        initializeAttributes();

        toastMessageErrorCreatingAccount = new CustomToastMessage(this, getString(getSomethingWentWrongMessage), 3);
        toastMessageNoInternetConnection = new CustomToastMessage(this, getString(getNoInternetConnectionAtSignMessage), 2);


        FirebaseUserManager.initializeFirebase();


    }

    private void initializeAttributes() {
        email = findViewById(editTextSignUpEmailAddress);
        password = findViewById(editTextSignUpPassword);
        confirmPassword = findViewById(editSignUpConfirmPassword);
        alreadyHaveAnAccount = findViewById(TextView_AlreadyHaveAccount);
        loginHere = findViewById(TextView_LoginHere);
        backButton = findViewById(BackButton);
        createButton = findViewById(CreateButton);
        circularProgressbar = findViewById(SignUpProgressBar);
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

    public void sendEmailVerification() {

    }

    public void verifyEmailVerification() {

    }

    private void SignUpUserToFirebase() {
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
    private void startLoading() {
        circularProgressbar.setVisibility(View.VISIBLE);
        email.setEnabled(false);
        password.setEnabled(false);
        confirmPassword.setEnabled(false);
        alreadyHaveAnAccount.setEnabled(false);
        loginHere.setEnabled(false);
        backButton.setEnabled(false);
        createButton.setEnabled(false);

    }

    private void finishLoading() {
        circularProgressbar.setVisibility(View.INVISIBLE);
        email.setEnabled(true);
        password.setEnabled(true);
        confirmPassword.setEnabled(true);
        alreadyHaveAnAccount.setEnabled(true);
        loginHere.setEnabled(true);
        backButton.setEnabled(true);
        createButton.setEnabled(true);
    }

    private void showMainScreen() {

        startActivity(new Intent(this, MainScreen.class));
        finish();
    }

}