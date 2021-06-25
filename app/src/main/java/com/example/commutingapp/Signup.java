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
        setContentView(activity_signup);

        name = findViewById(editTextSignUpName);
        email = findViewById(editTextSignUpEmailAddress);
        phoneNumber = findViewById(editTextSignUpPhone);
        password = findViewById(editTextSignUpPassword);
        confirmPassword = findViewById(editSignUpConfirmPassword);

        circularProgressbar = findViewById(SignUpProgressBar);

        toastMessageErrorCreatingAccount = new CustomToastMessage(this, getString(getSomethingWentWrongMessage), 3);
        toastMessageNoInternetConnection = new CustomToastMessage(this, getString(getNoInternetConnectionAtSignMessage), 2);

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
        userManager = new UserManager(name, email, phoneNumber, password, confirmPassword);
        if (userManager.UserInputRequirementsFailedAtSignUp()) {
            return;
        }

        if (!connectionManager.PhoneHasInternetConnection()) {
            toastMessageNoInternetConnection.showToast();
            return;
        }


        toastMessageNoInternetConnection.hideToast();
        SignUpUserToFirebase();



    }


    private void SignUpUserToFirebase(){
        String userEmail = email.getText().toString().trim();
        String userConfirmPassword = confirmPassword.getText().toString().trim();
        firebaseUserManager.getFirebaseInstance().createUserWithEmailAndPassword(userEmail, userConfirmPassword).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                circularProgressbar.setVisibility(View.VISIBLE);
                firebaseUserManager.getCurrentUser();
                toastMessageErrorCreatingAccount.hideToast();
                showMainScreen();
                return;
            }

            toastMessageErrorCreatingAccount.showToast();
        });
    }



    private void showMainScreen() {

        startActivity(new Intent(this, MainScreen.class));
        finish();
    }

}