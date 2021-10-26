package com.example.commutingapp.views.ui.activities;

import static com.example.commutingapp.R.string.sendingEmailErrorMessage;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.commutingapp.data.firebase.auth.FirebaseAuthenticatorWrapper;
import com.example.commutingapp.data.firebase.auth.UserAuthenticationProcessor;
import com.example.commutingapp.data.firebase.usr.FirebaseUserWrapper;
import com.example.commutingapp.data.firebase.usr.UserDataProcessor;
import com.example.commutingapp.data.firebase.usr.UserEmailProcessor;
import com.example.commutingapp.utils.InternetConnection.Connection;
import com.example.commutingapp.utils.input_validator.users.UserInputValidate;
import com.example.commutingapp.utils.input_validator.users.ValidateInputModel;
import com.example.commutingapp.databinding.ActivitySignupBinding;
import com.example.commutingapp.databinding.CircularProgressbarBinding;
import com.example.commutingapp.utils.ui_utilities.ActivitySwitch;
import com.example.commutingapp.utils.ui_utilities.ScreenDimension;
import com.example.commutingapp.viewmodels.SignUpViewModel;
import com.example.commutingapp.views.dialogs.DialogDirector;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.UserInfo;


import java.util.List;
import java.util.Objects;


public class Signup extends AppCompatActivity {


    private DialogDirector dialogDirector;
    private ActivitySignupBinding activitySignupBinding;
    private CircularProgressbarBinding circularProgressbarBinding;

    private FirebaseUserWrapper firebaseUser;

    private UserDataProcessor<List<UserInfo>> userData;
    private UserAuthenticationProcessor<Task<AuthResult>> userAuth;
    private UserEmailProcessor<Task<Void>> userEmail;
    private SignUpViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeAttributes();
        subscribeToObservers();
    }
    private void subscribeToObservers(){
        observeEmailVerification();
        observeInternet();
        observeExceptionMessage();
    }
    private void observeEmailVerification(){
        viewModel.sendEmailOnSuccess().observe(this, task-> showEmailSentActivity());
        viewModel.sendEmailOnFailed().observe(this, task-> dialogDirector.showErrorDialog("Error", getString(sendingEmailErrorMessage)));
    }
    private void observeInternet(){
        viewModel.noInternetStatus().observe(this, task-> dialogDirector.constructNoInternetDialog());
    }
    private void observeExceptionMessage(){
        viewModel.getExceptionMessage().observe(this,errorMessage->dialogDirector.showErrorDialog("Error", Objects.requireNonNull(errorMessage)));
    }

    private void initializeAttributes() {
        ScreenDimension.INSTANCE.setWindowToFullScreen(getWindow());
        activitySignupBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        circularProgressbarBinding = CircularProgressbarBinding.bind(activitySignupBinding.getRoot());
        setContentView(activitySignupBinding.getRoot());
        dialogDirector = new DialogDirector(this);
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        firebaseUser = new FirebaseUserWrapper();
        userData = new UserDataProcessor(firebaseUser);
        userAuth = new UserAuthenticationProcessor(new FirebaseAuthenticatorWrapper());
        userEmail = new UserEmailProcessor(firebaseUser);
    }

    @Override
    public void onBackPressed() {
        showSignInActivity();
    }


    public void backToSignIn(View view) {
        showSignInActivity();
    }

    public void CreateButtonClicked(View view) {


        UserInputValidate input = new UserInputValidate(new ValidateInputModel(this,
                activitySignupBinding.editTextSignUpEmailAddress,
                activitySignupBinding.editTextSignUpPassword,
                activitySignupBinding.editTextSignUpConfirmPassword));


        if (!input.isValid()) {
            return;
        }
        if (!Connection.INSTANCE.hasInternetConnection(this)) {
            dialogDirector.constructNoInternetDialog();
            return;
        }

        if (userData.hasAccountRemainingInCache() && isUserCreatedNewAccount()) {
            signOutPreviousAccount();
            ProceedToSignUp();
            return;
        }
        ProceedToSignUp();

    }



    private void signOutPreviousAccount() {

        userAuth.signOut();
    }

    private boolean isUserCreatedNewAccount() {
        String currentEmail = Objects.requireNonNull(activitySignupBinding.editTextSignUpEmailAddress.getText()).toString().trim();
        String previousEmail = userEmail.getUserEmail();
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

        ActivitySwitch.INSTANCE.startActivityOf(this, EmailSent.class);
    }

    private void showSignInActivity() {

        ActivitySwitch.INSTANCE.startActivityOf(this, SignIn.class);
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