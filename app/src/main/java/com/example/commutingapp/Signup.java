package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;

import java.util.Objects;

import FirebaseUserManager.FirebaseUserManager;
import InternetConnection.ConnectionManager;
import Logger.CustomDialogs;
import Logger.CustomToastMessage;
import MenuButtons.CustomBackButton;
import MenuButtons.backButton;
import Screen.ScreenDimension;
import ValidateUser.UserManager;

import static com.example.commutingapp.R.id.BackButton;
import static com.example.commutingapp.R.id.CreateButton;
import static com.example.commutingapp.R.id.LoadingProgressBar;
import static com.example.commutingapp.R.id.TextView_AlreadyHaveAccount;
import static com.example.commutingapp.R.id.TextView_LoginHere;
import static com.example.commutingapp.R.id.editSignUpConfirmPassword;
import static com.example.commutingapp.R.id.editTextSignUpEmailAddress;
import static com.example.commutingapp.R.id.editTextSignUpPassword;
import static com.example.commutingapp.R.layout.activity_signup;
import static com.example.commutingapp.R.string.doubleTappedMessage;
import static com.example.commutingapp.R.string.sendingEmailErrorMessage;


public class Signup extends AppCompatActivity {


    private EditText email, password, confirmPassword;
    private Button back_Button, createButton;
    private TextView alreadyHaveAnAccount, loginHere;
    private ConnectionManager connectionManager;
    private ProgressBar circularProgressbar;
    private CustomToastMessage toastMessageBackButton;
    private CustomDialogs customPopupDialog;

    private void initializeAttributes() {
        email = findViewById(editTextSignUpEmailAddress);
        password = findViewById(editTextSignUpPassword);
        confirmPassword = findViewById(editSignUpConfirmPassword);
        alreadyHaveAnAccount = findViewById(TextView_AlreadyHaveAccount);
        loginHere = findViewById(TextView_LoginHere);
        back_Button = findViewById(BackButton);
        createButton = findViewById(CreateButton);
        circularProgressbar = findViewById(LoadingProgressBar);
        customPopupDialog = new CustomDialogs(this);
        toastMessageBackButton = new CustomToastMessage(this, getString(doubleTappedMessage), 10);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ScreenDimension(getWindow()).windowToFullScreen();
        setContentView(activity_signup);
        initializeAttributes();


        FirebaseUserManager.initializeFirebase();

    }

    @Override
    public void onBackPressed() {

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

    @Override
    protected void onStart() {
        super.onStart();
        connectionManager = new ConnectionManager(this);
    }

    public void backToSignInButton(View view) {
        startActivity(new Intent(this, SignIn.class));
        finish();
    }

    public void CreateButtonClicked(View view) {
        UserManager userManager = new UserManager(this, email, password, confirmPassword);
        if (userManager.userInputRequirementsFailedAtSignUp()) {
            return;
        }
        if (!connectionManager.PhoneHasInternetConnection()) {
            showNoInternetDialog();
            return;
        }
        FirebaseUserManager.getCurrentUser();
        if (FirebaseUserManager.isUserAlreadySignedIn() && isUserCreatedNewAccount()) {
            signOutPreviousAccount();
            ProceedToSignUp();
            return;
        }
        ProceedToSignUp();
    }

    public void GoToSettingsClicked(View view) {
        startActivity(new Intent(Settings.ACTION_SETTINGS));
    }

    private void signOutPreviousAccount() {
        FirebaseUserManager.getFirebaseAuthInstance().signOut();
    }

    private boolean isUserCreatedNewAccount() {
        String currentEmail = email.getText().toString().trim();
        String previousEmail = FirebaseUserManager.getFirebaseUserInstance().getEmail();
        return !Objects.equals(previousEmail, currentEmail);
    }

    private void sendEmailVerificationToUser() {
        FirebaseUserManager.getFirebaseUserInstance().sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showEmailSentDialog();
                return;
            }
            customPopupDialog.showErrorDialog("Error", getString(sendingEmailErrorMessage));
        });
    }

    private void ProceedToSignUp() {
        String userEmail = email.getText().toString().trim();
        String userConfirmPassword = confirmPassword.getText().toString().trim();

        startLoading();
        FirebaseUserManager.getFirebaseAuthInstance().createUserWithEmailAndPassword(userEmail, userConfirmPassword).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                FirebaseUserManager.getCurrentUser();
                finishLoading();
                sendEmailVerificationToUser();

                return;

            }

            if (task.getException() != null) {
                finishLoading();
                handleTaskExceptionResults(task);
            }

        });
    }

    private void handleTaskExceptionResults(Task<?> task) {
        try {
            throw task.getException();
        } catch (FirebaseNetworkException firebaseNetworkException) {
            showNoInternetDialog();
        } catch (Exception ex) {
            customPopupDialog.showErrorDialog("Error", task.getException().getMessage());
        }
    }

    private void startLoading() {
        circularProgressbar.setVisibility(View.VISIBLE);
        email.setEnabled(false);
        password.setEnabled(false);
        confirmPassword.setEnabled(false);
        alreadyHaveAnAccount.setEnabled(false);
        loginHere.setEnabled(false);
        back_Button.setEnabled(false);
        createButton.setEnabled(false);

    }

    private void finishLoading() {
        circularProgressbar.setVisibility(View.INVISIBLE);
        email.setEnabled(true);
        password.setEnabled(true);
        confirmPassword.setEnabled(true);
        alreadyHaveAnAccount.setEnabled(true);
        loginHere.setEnabled(true);
        back_Button.setEnabled(true);
        createButton.setEnabled(true);
    }

    private void showNoInternetDialog() {

        startActivity(new Intent(this, NoInternet.class));
    }

    private void showEmailSentDialog() {
        startActivity(new Intent(this, EmailSent.class));
        finish();
    }


}