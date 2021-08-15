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
import UI.ActivitySwitcher;
import UI.ScreenDimension;
import Users.UserManager;

import static com.example.commutingapp.R.string.doubleTappedMessage;
import static com.example.commutingapp.R.string.sendingEmailErrorMessage;


public class Signup extends AppCompatActivity {


    private CustomDialogs customPopupDialog;
    private ActivitySignupBinding activitySignupBinding;
    private CircularProgressbarBinding circularProgressbarBinding;



    @Override
    protected void onDestroy() {
       destroyCircularProgressBarBinding();
       destroySignUpBinding();
        super.onDestroy();
    }

    private void destroySignUpBinding(){
        activitySignupBinding = null;
    }
    private void destroyCircularProgressBarBinding(){
        circularProgressbarBinding = null;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeLocalAttributes();
        FirebaseUserManager.initializeFirebase();

    }
    private void initializeLocalAttributes() {
        new ScreenDimension(getWindow()).setWindowToFullScreen();
        activitySignupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        circularProgressbarBinding = CircularProgressbarBinding.bind(activitySignupBinding.getRoot());
        setContentView(activitySignupBinding.getRoot());
        customPopupDialog = new CustomDialogs(this);

    }

    @Override
    public void onBackPressed() {

        showSignInActivity();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void backToSignIn(View view) {
        showSignInActivity();
    }

    public void CreateButtonClicked(View view) {
        UserManager userManager = new UserManager(this,
                activitySignupBinding.editTextSignUpEmailAddress,
                activitySignupBinding.editTextSignUpPassword,
                activitySignupBinding.editTextSignUpConfirmPassword);
        if (userManager.signUpValidationFail()) {
            return;
        }
        if (!new ConnectionManager(this).internetConnectionAvailable()) {
            showNoInternetActivity();
            return;
        }
        FirebaseUserManager.getCreatedUserAccount();
        if (FirebaseUserManager.userAlreadySignIn() && isUserCreatedNewAccount()) {
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
                showEmailSentActivity();
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
                FirebaseUserManager.getCreatedUserAccount();
                sendEmailVerificationToUser();
                return;

            }

            if (task.getException() != null) {
                finishLoading();
                handleSignUpExceptionResults(task);
            }

        });
    }

    private void handleSignUpExceptionResults(Task<?> task) {
        try {
            throw Objects.requireNonNull(task.getException());
        } catch (FirebaseNetworkException firebaseNetworkException) {
            showNoInternetActivity();
        } catch (Exception ex) {
            customPopupDialog.showErrorDialog("Error", Objects.requireNonNull(task.getException().getMessage()));
        }
    }

    private void startLoading() {

        makeLoading(false, View.VISIBLE);
    }

    private void finishLoading() {

        makeLoading(true, View.INVISIBLE);
    }

    private void makeLoading(boolean attributesVisibility, int progressBarVisibility) {
        circularProgressbarBinding.circularProgressBar.setVisibility(progressBarVisibility);
        activitySignupBinding.editTextSignUpEmailAddress.setEnabled(attributesVisibility);
        activitySignupBinding.editTextSignUpPassword.setEnabled(attributesVisibility);
        activitySignupBinding.editTextSignUpConfirmPassword.setEnabled(attributesVisibility);
        activitySignupBinding.TextViewAlreadyHaveAccount.setEnabled(attributesVisibility);
        activitySignupBinding.TextViewLoginHere.setEnabled(attributesVisibility);
        activitySignupBinding.BackButton.setEnabled(attributesVisibility);
        activitySignupBinding.CreateButton.setEnabled(attributesVisibility);
    }

    private void showNoInternetActivity() {

        ActivitySwitcher.INSTANCE.startActivityOf(this,NoInternet.class);
    }

    private void showEmailSentActivity() {

        ActivitySwitcher.INSTANCE.startActivityOf(this,this,EmailSent.class);
    }
    private void showSignInActivity(){

        ActivitySwitcher.INSTANCE.startActivityOf(this,this,SignIn.class);
    }


}