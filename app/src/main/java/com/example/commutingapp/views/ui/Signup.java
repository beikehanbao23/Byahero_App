package com.example.commutingapp.views.ui;

import static com.example.commutingapp.R.string.sendingEmailErrorMessage;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.commutingapp.utils.InputValidator.users.UserValidatorManager;
import com.example.commutingapp.utils.InputValidator.users.UserValidatorModel;
import com.example.commutingapp.databinding.ActivitySignupBinding;
import com.example.commutingapp.databinding.CircularProgressbarBinding;
import com.example.commutingapp.data.Auth.AuthenticationManager;
import com.example.commutingapp.utils.InternetConnection.ConnectionManager;
import com.example.commutingapp.utils.ui_utilities.ActivitySwitcher;
import com.example.commutingapp.utils.ui_utilities.ScreenDimension;
import com.example.commutingapp.viewmodels.SignUpViewModel;
import com.example.commutingapp.views.Logger.CustomDialogProcessor;


import java.util.Objects;


public class Signup extends AppCompatActivity {


    private CustomDialogProcessor customDialogProcessor;
    private ActivitySignupBinding activitySignupBinding;
    private CircularProgressbarBinding circularProgressbarBinding;

    private SignUpViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeAttributes();
        AuthenticationManager.initializeFirebaseApp();
        initializeObservers();
    }
    private void initializeObservers(){
        observeEmailVerification();
        observeInternet();
        observeExceptionMessage();
    }
    private void observeEmailVerification(){
        viewModel.sendEmailOnSuccess().observe(this, task-> showEmailSentActivity());
        viewModel.sendEmailOnFailed().observe(this, task-> customDialogProcessor.showErrorDialog("Error", getString(sendingEmailErrorMessage)));
    }
    private void observeInternet(){
        viewModel.noInternetStatus().observe(this, task-> customDialogProcessor.showNoInternetDialog());
    }
    private void observeExceptionMessage(){
        viewModel.getExceptionMessage().observe(this,errorMessage->customDialogProcessor.showErrorDialog("Error", Objects.requireNonNull(errorMessage)));
    }

    private void initializeAttributes() {
        new ScreenDimension(getWindow()).setWindowToFullScreen();
        activitySignupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        circularProgressbarBinding = CircularProgressbarBinding.bind(activitySignupBinding.getRoot());
        setContentView(activitySignupBinding.getRoot());
        customDialogProcessor = new CustomDialogProcessor(this);
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
    }

    @Override
    public void onBackPressed() {
        showSignInActivity();
    }


    public void backToSignIn(View view) {
        showSignInActivity();
    }

    public void CreateButtonClicked(View view) {


        UserValidatorManager user = new UserValidatorManager(new UserValidatorModel(this,
                activitySignupBinding.editTextSignUpEmailAddress,
                activitySignupBinding.editTextSignUpPassword,
                activitySignupBinding.editTextSignUpConfirmPassword));


        if (user.signUpValidationFail()) {
            return;
        }
        if (noInternetConnection()) {
            customDialogProcessor.showNoInternetDialog();
            return;
        }
        AuthenticationManager.getCreatedUserAccount();
        if (AuthenticationManager.hasAccountRemainingInCache() && isUserCreatedNewAccount()) {
            signOutPreviousAccount();
            ProceedToSignUp();
            return;
        }
        ProceedToSignUp();

    }

    private boolean noInternetConnection() {
        return !new ConnectionManager(this).internetConnectionAvailable();
    }

    private void signOutPreviousAccount() {
        AuthenticationManager.getFirebaseAuthInstance().signOut();
    }

    private boolean isUserCreatedNewAccount() {
        String currentEmail = Objects.requireNonNull(activitySignupBinding.editTextSignUpEmailAddress.getText()).toString().trim();
        String previousEmail = AuthenticationManager.getFirebaseUserInstance().getEmail();
        return !Objects.equals(previousEmail, currentEmail);
    }


    private void ProceedToSignUp() {
        String userEmail = Objects.requireNonNull(activitySignupBinding.editTextSignUpEmailAddress.getText()).toString().trim();
        String userConfirmPassword = Objects.requireNonNull(activitySignupBinding.editTextSignUpConfirmPassword.getText()).toString().trim();

        showLoading();
        viewModel.signUpAccount(userEmail,userConfirmPassword);
        finishLoading();
    }




    private void showLoading() {
        processLoading(false, View.VISIBLE);
    }

    private void finishLoading(){
        processLoading(true,View.INVISIBLE);
    }

    private void processLoading(boolean attributesVisibility, int progressBarVisibility) {
        circularProgressbarBinding.circularProgressBar.setVisibility(progressBarVisibility);
        activitySignupBinding.editTextSignUpEmailAddress.setEnabled(attributesVisibility);
        activitySignupBinding.editTextSignUpPassword.setEnabled(attributesVisibility);
        activitySignupBinding.editTextSignUpConfirmPassword.setEnabled(attributesVisibility);
        activitySignupBinding.TextViewAlreadyHaveAccount.setEnabled(attributesVisibility);
        activitySignupBinding.TextViewLoginHere.setEnabled(attributesVisibility);
        activitySignupBinding.BackButton.setEnabled(attributesVisibility);
        activitySignupBinding.CreateButton.setEnabled(attributesVisibility);
    }


    private void showEmailSentActivity() {

        ActivitySwitcher.INSTANCE.startActivityOf(this, this, EmailSent.class);
    }

    private void showSignInActivity() {

        ActivitySwitcher.INSTANCE.startActivityOf(this, this, SignIn.class);
    }

    @Override protected void onDestroy() {
        destroyBinding();
        super.onDestroy();
    }

    private void destroyBinding() {
        activitySignupBinding = null;
        circularProgressbarBinding = null;
    }

}