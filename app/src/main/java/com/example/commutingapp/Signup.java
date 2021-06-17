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


public class Signup extends AppCompatActivity {

    private EditText name, email, phoneNumber, password, confirmPassword;
    private FirebaseUserManager firebaseUserManager;
    private CustomToastMessage toastMessageErrorCreatingAccount;
    private CustomToastMessage toastMessageNoInternetConnection;
    private ConnectionManager connectionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.EditTextName);
        email = findViewById(R.id.EditTextEmail);
        phoneNumber = findViewById(R.id.editTextPhone);
        password = findViewById(R.id.editTextPassword);
        confirmPassword = findViewById(R.id.EditTextConfirmPassword);

        toastMessageErrorCreatingAccount = new CustomToastMessage(this, "Something went wrong with creating your account. Please try again later.", 3);
        toastMessageNoInternetConnection = new CustomToastMessage(this, LoggerErrorMessage.getNoInternetConnectionErrorMessage(),2);

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
        String userEmail = email.getText().toString().trim();
        String userConfirmPassword = confirmPassword.getText().toString().trim();

        firebaseUserManager.verifyUserForSignUp(name, email, phoneNumber, password, confirmPassword);
        if (firebaseUserManager.UserInputRequirementsFailedAtSignUp()) {
            return;
        }

        if(!connectionManager.PhoneHasInternetConnection()){
            toastMessageNoInternetConnection.showMessage();
            return;
        }


        firebaseUserManager.getFirebaseAuthenticate().createUserWithEmailAndPassword(userEmail, userConfirmPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseUserManager.getCurrentUser();
                toastMessageErrorCreatingAccount.hideMessage();
                showMainScreen();
                return;
            }
            toastMessageErrorCreatingAccount.showMessage();
            toastMessageNoInternetConnection.hideMessage();
        });

    }

    private void showMainScreen() {
        startActivity(new Intent(this, MainScreen.class));
        finish();
    }

}