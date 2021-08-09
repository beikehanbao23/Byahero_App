package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.commutingapp.databinding.ActivitySignupBinding;
import com.example.commutingapp.databinding.CircularProgressbarBinding;
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

import static com.example.commutingapp.R.string.doubleTappedMessage;
import static com.example.commutingapp.R.string.sendingEmailErrorMessage;


public class Signup extends AppCompatActivity {


    private CustomToastMessage toastMessageBackButton;
    private CustomDialogs customPopupDialog;
    private ActivitySignupBinding activitySignupBinding;
    private CircularProgressbarBinding circularProgressbarBinding;

    private void initializeAttributes() {

        customPopupDialog = new CustomDialogs(this);
        toastMessageBackButton = new CustomToastMessage(this, getString(doubleTappedMessage), 10);
    }

    @Override
    protected void onDestroy() {
        activitySignupBinding = null;
        circularProgressbarBinding = null;
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ScreenDimension(getWindow()).windowToFullScreen();
        activitySignupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        circularProgressbarBinding = CircularProgressbarBinding.bind(activitySignupBinding.getRoot());
        setContentView(activitySignupBinding.getRoot());
        initializeAttributes();


        FirebaseUserManager.initializeFirebase();

    }

    @Override
    public void onBackPressed() {

        showNoInternetDialog();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void backToSignIn(View view) {
        startActivity(new Intent(this, SignIn.class));
        finish();
    }

    public void CreateButtonClicked(View view) {
        UserManager userManager = new UserManager(this,
                activitySignupBinding.editTextSignUpEmailAddress,
                activitySignupBinding.editTextSignUpPassword,
                activitySignupBinding.editTextSignUpConfirmPassword);
        if (userManager.userInputRequirementsFailedAtSignUp()) {
            return;
        }
        if (!new ConnectionManager(this).PhoneHasInternetConnection()) {
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
        String currentEmail = Objects.requireNonNull(activitySignupBinding.editTextSignUpEmailAddress.getText()).toString().trim();
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
        String userEmail = Objects.requireNonNull(activitySignupBinding.editTextSignUpEmailAddress.getText()).toString().trim();
        String userConfirmPassword = Objects.requireNonNull(activitySignupBinding.editTextSignUpConfirmPassword.getText()).toString().trim();

        startLoading();
        FirebaseUserManager.getFirebaseAuthInstance().createUserWithEmailAndPassword(userEmail, userConfirmPassword).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                FirebaseUserManager.getCurrentUser();
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
            throw Objects.requireNonNull(task.getException());
        } catch (FirebaseNetworkException firebaseNetworkException) {
            showNoInternetDialog();
        } catch (Exception ex) {
            customPopupDialog.showErrorDialog("Error", Objects.requireNonNull(task.getException().getMessage()));
        }
    }

    private void startLoading() {

        setLoading(false, View.VISIBLE);
    }

    private void finishLoading() {

        setLoading(true, View.INVISIBLE);
    }

    private void setLoading(boolean visible, int progressBarVisibility) {
        circularProgressbarBinding.circularProgressBar.setVisibility(progressBarVisibility);
        activitySignupBinding.editTextSignUpEmailAddress.setEnabled(visible);
        activitySignupBinding.editTextSignUpPassword.setEnabled(visible);
        activitySignupBinding.editTextSignUpConfirmPassword.setEnabled(visible);
        activitySignupBinding.TextViewAlreadyHaveAccount.setEnabled(visible);
        activitySignupBinding.TextViewLoginHere.setEnabled(visible);
        activitySignupBinding.BackButton.setEnabled(visible);
        activitySignupBinding.CreateButton.setEnabled(visible);
    }

    private void showNoInternetDialog() {
        startActivity(new Intent(this, NoInternet.class));
    }

    private void showEmailSentDialog() {
        startActivity(new Intent(this, EmailSent.class));
        finish();
    }


}