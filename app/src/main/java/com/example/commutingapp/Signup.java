package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.rejowan.cutetoast.CuteToast;

import FirebaseUserManager.FirebaseUserManager;
import InternetConnection.ConnectionManager;
import Logger.CustomToastMessage;
import ValidateUser.UserManager;

import static com.example.commutingapp.R.id.*;
import static com.example.commutingapp.R.layout.activity_signup;
import static com.example.commutingapp.R.string.*;

public class Signup extends AppCompatActivity {

    private EditText email, password, confirmPassword;
    private Button backButton, createButton;
    private TextView alreadyHaveAnAccount, loginHere;
    private CustomToastMessage toastMessageNoInternetConnection;
    private ConnectionManager connectionManager;
    private ProgressBar circularProgressbar;
    private UserManager userManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(activity_signup);

        initializeAttributes();
        //TODO this is wrong
        toastMessageNoInternetConnection = new CustomToastMessage(this, getString(getNoInternetConnectionAtSignMessage), 2);

        FirebaseUserManager.initializeFirebase();


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

    @Override
    protected void onResume() {
        super.onResume();

    /*
    TODO:
    when user back to the application after verifying email then it automatically go to
    main screen without clicking the create button again.
     */
    }

    /*
    TODO recheck error message sign in
     */
    private void initializeAttributes() {
        email = findViewById(editTextSignUpEmailAddress);
        password = findViewById(editTextSignUpPassword);
        confirmPassword = findViewById(editSignUpConfirmPassword);
        alreadyHaveAnAccount = findViewById(TextView_AlreadyHaveAccount);
        loginHere = findViewById(TextView_LoginHere);
        backButton = findViewById(BackButton);
        createButton = findViewById(CreateButton);
        circularProgressbar = findViewById(LoadingProgressBar);
    }

    public void backToSignInButton(View view) {
        startActivity(new Intent(this, SignIn.class));
        finish();

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

        FirebaseUserManager.getCurrentUser();
        if (FirebaseUserManager.isUserAlreadySignedIn()) {// email_1
            Log.e(getClass().getName(), "Email instance is" + FirebaseUserManager.getFirebaseUser().getEmail());

            if(isUserCreatedNewAccount()){
            signOutPreviousAccount();
            SignUpUser();
            return;
            }

            SignUpAndVerifyUser();
            return;
        }

        SignUpUser();


    }
    private void signOutPreviousAccount(){
        FirebaseUserManager.getFirebaseAuth().signOut();
    }
    private boolean isUserCreatedNewAccount(){
        String userEmail = email.getText().toString().trim();
        return !FirebaseUserManager.getFirebaseUser().getEmail().equals(userEmail);
    }

    private void sendEmailVerificationToUser() {
        FirebaseUserManager.getFirebaseUser().sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                CuteToast.ct(this, getString(getVerifyEmailToContinueMessage), Toast.LENGTH_SHORT, 1, true).show();
                return;
            }
            CuteToast.ct(this, getString(getSendingEmailErrorMessage), Toast.LENGTH_LONG, 3, true).show();
        });
    }

    private void SignUpAndVerifyUser() {

        FirebaseUserManager.getFirebaseUser().reload().addOnCompleteListener(task -> {
            if (task.isSuccessful() && FirebaseUserManager.getFirebaseUser().isEmailVerified()) {
                showMainScreen();
                return;
            }
            //TODO: CHANGE UI
            CuteToast.ct(this, getString(getVerifyEmailToContinueMessage), Toast.LENGTH_SHORT, 1, true).show();
            Log.e("Signup", "At click button user instance " + FirebaseUserManager.getFirebaseUser().getEmail());
        });
    }

    private void SignUpUser() {
        String userEmail = email.getText().toString().trim();
        String userConfirmPassword = confirmPassword.getText().toString().trim();

        startLoading();
        FirebaseUserManager.getFirebaseAuth().createUserWithEmailAndPassword(userEmail, userConfirmPassword).addOnCompleteListener(task -> {
        //TODO change animation while waiting for user to verify email
            if (task.isSuccessful()) {
                FirebaseUserManager.getCurrentUser();
                sendEmailVerificationToUser();
                finishLoading();
                return;

            }

            if (task.getException() != null) {
                finishLoading();
                Log.e("Signup", "GOTCHA! ERROR AT SIGNUP USER FUNCTION");
                handleTaskExceptionResults(task);
            }

        });
    }

    public void handleTaskExceptionResults(Task<AuthResult> task) {
        try {
            throw task.getException();
        } catch (FirebaseNetworkException firebaseNetworkException) {
            toastMessageNoInternetConnection.showToastWithLimitedTimeThenClose(2250);
        } catch (Exception ex) {
            CuteToast.ct(this, task.getException().getMessage(), Toast.LENGTH_SHORT, 3, true).show();
        }
    }

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