package com.example.commutingapp.views.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.commutingapp.data.users.UserValidatorModel;
import com.example.commutingapp.databinding.ActivitySignupBinding;
import com.example.commutingapp.databinding.CircularProgressbarBinding;
import com.example.commutingapp.viewmodels.SignUpViewModel;
import com.example.commutingapp.views.Logger.CustomDialogProcessor;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import java.util.Objects;
import com.example.commutingapp.utils.FirebaseUserManager.*;
import com.example.commutingapp.utils.InternetConnection.*;
import com.example.commutingapp.utils.ui_utilities.ActivitySwitcher;
import com.example.commutingapp.utils.ui_utilities.AttributesInitializer;
import com.example.commutingapp.utils.ui_utilities.BindingDestroyer;
import com.example.commutingapp.utils.ui_utilities.LoadingScreen;
import com.example.commutingapp.utils.ui_utilities.ScreenDimension;
import com.example.commutingapp.data.users.UserValidatorManager;

import static com.example.commutingapp.R.string.sendingEmailErrorMessage;


public class Signup extends AppCompatActivity implements LoadingScreen, BindingDestroyer, AttributesInitializer {


    private CustomDialogProcessor customDialogProcessor;
    private ActivitySignupBinding activitySignupBinding;
    private CircularProgressbarBinding circularProgressbarBinding;
    private SignUpViewModel viewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeAttributes();
        AuthenticationManager.initializeFirebaseApp();

    }

    @Override
    public void initializeAttributes() {
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
            showNoInternetActivity();
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
    private boolean noInternetConnection(){
        return !new ConnectionManager(this).internetConnectionAvailable();
    }
    public void GoToSettingsClicked(View view) {
        startActivity(new Intent(Settings.ACTION_SETTINGS));
    }
    private void signOutPreviousAccount() {
        AuthenticationManager.getFirebaseAuthInstance().signOut();
    }
    private boolean isUserCreatedNewAccount() {
        String currentEmail = Objects.requireNonNull(activitySignupBinding.editTextSignUpEmailAddress.getText()).toString().trim();
        String previousEmail = AuthenticationManager.getFirebaseUserInstance().getEmail();
        return !Objects.equals(previousEmail, currentEmail);
    }





    private void sendEmailVerificationToUser() {
        AuthenticationManager.getFirebaseUserInstance().sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showEmailSentActivity();
                return;
            }
            customDialogProcessor.showErrorDialog("Error", getString(sendingEmailErrorMessage));
        });
    }


    private void ProceedToSignUp() {
        String userEmail = Objects.requireNonNull(activitySignupBinding.editTextSignUpEmailAddress.getText()).toString().trim();
        String userConfirmPassword = Objects.requireNonNull(activitySignupBinding.editTextSignUpConfirmPassword.getText()).toString().trim();

        showLoading();


        AuthenticationManager.getFirebaseAuthInstance().createUserWithEmailAndPassword(userEmail, userConfirmPassword).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                AuthenticationManager.getCreatedUserAccount();
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
            customDialogProcessor.showErrorDialog("Error", Objects.requireNonNull(task.getException().getMessage()));
        }
    }





    @Override public void showLoading() {

        processLoading(false, View.VISIBLE);
    }
    @Override public void finishLoading() {

        processLoading(true, View.INVISIBLE);
    }
    @Override public void processLoading(boolean attributesVisibility, int progressBarVisibility) {
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
        customDialogProcessor.showNoInternetDialog();
    }
    private void showEmailSentActivity() {

        ActivitySwitcher.INSTANCE.startActivityOf(this,this, EmailSent.class);
    }
    private void showSignInActivity(){

        ActivitySwitcher.INSTANCE.startActivityOf(this,this,SignIn.class);
    }
    @Override protected void onDestroy() {
        destroyBinding();
        super.onDestroy();
    }
    @Override public void destroyBinding(){
        activitySignupBinding = null;
        circularProgressbarBinding = null;
    }

}