package com.example.commutingapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.rejowan.cutetoast.CuteToast;

import FirebaseUserManager.FirebaseUserManager;
import InternetConnection.ConnectionManager;
import Logger.CustomToastMessage;
import MenuButtons.BackButtonDoubleClicked;
import MenuButtons.CustomBackButton;
import MenuButtons.backButton;
import ValidateUser.UserManager;

import static com.example.commutingapp.R.id.FacebookButton;
import static com.example.commutingapp.R.id.GoogleButton;
import static com.example.commutingapp.R.id.LoadingProgressBar;
import static com.example.commutingapp.R.id.LogInButton;
import static com.example.commutingapp.R.id.TextViewSignUp;
import static com.example.commutingapp.R.id.TextView_DontHaveAnAccount;
import static com.example.commutingapp.R.id.editLogin_TextPassword;
import static com.example.commutingapp.R.id.editlogin_TextEmail;
import static com.example.commutingapp.R.layout.activity_sign_in;
import static com.example.commutingapp.R.layout.custom_emailsent_dialog;
import static com.example.commutingapp.R.layout.custom_no_internet_dialog;
import static com.example.commutingapp.R.string.getDisabledAccountMessage;
import static com.example.commutingapp.R.string.getDoubleTappedMessage;
import static com.example.commutingapp.R.string.getIncorrectEmailOrPasswordMessage;


public class SignIn extends AppCompatActivity implements BackButtonDoubleClicked {

    private EditText email, password;
    private Button facebookButton, googleButton, loginButton;
    private Dialog noInternetDialog, emailSentDialog;
    private TextView dontHaveAnAccountTextView, signUpTextView;
    private CustomToastMessage toastMessageBackButton;
    private ConnectionManager connectionManager;
    private ProgressBar circularProgressBar;
    private UserManager userManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(activity_sign_in);
        initializeAttributes();

        FirebaseUserManager.initializeFirebase();
        noInternetDialog.setContentView(custom_no_internet_dialog);
        emailSentDialog.setContentView(custom_emailsent_dialog);


    }

    @Override
    protected void onDestroy() {
        if(noInternetDialog.isShowing()){
            noInternetDialog.dismiss();
        }
        if(emailSentDialog.isShowing()){
            emailSentDialog.dismiss();
        }
        super.onDestroy();
    }

    public void GoToSettingsClicked(View view) {
        startActivity(new Intent(Settings.ACTION_SETTINGS));
    }


    public void SignUpTextClicked(View view) {
        this.startActivity(new Intent(this, Signup.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectionManager = new ConnectionManager(this);
    }


    public void retryButtonClicked(View view) {
        if (connectionManager.PhoneHasInternetConnection() && noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }

    }


    @Override
    public void onBackPressed() {
        backButtonClicked();
    }


    public void SignInButtonIsClicked(View view) {

        userManager = new UserManager(getBaseContext(), email, password);
        if (userManager.UserInputRequirementsFailedAtSignIn()) {
            return;
        }

        if (!connectionManager.PhoneHasInternetConnection()) {
            showNoInternetDialog();
            return;
        }

        ProceedToLogin();
    }
    //TODO retest exceptions


    @Override
    public void backButtonClicked() {

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


    public void refreshButtonClicked(View view) {
        FirebaseUserManager.getFirebaseUser().reload().addOnCompleteListener(task -> {
            if (task.isSuccessful() && FirebaseUserManager.getFirebaseUser().isEmailVerified()) {
                showMainScreen();
            }
        });
    }

    //TODO fix later
    public void resendEmailIsClicked(View view) {
        FirebaseUserManager.getFirebaseUser().sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //show cutetoast
                //start timer
            }
        });
    }


    private void ProceedToLogin() {

        String userUsername = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        startLoading();
        FirebaseUserManager.getFirebaseAuth().signInWithEmailAndPassword(userUsername, userPassword).addOnCompleteListener(this, signInTask -> {
            if (signInTask.isSuccessful()) {
                FirebaseUserManager.getCurrentUser();
                VerifyUserEmail();
                return;
            }
            if (signInTask.getException() != null) {
                finishLoading();
                handleTaskExceptionResults(signInTask);
            }
        });

    }


    private void VerifyUserEmail() {
        FirebaseUserManager.getFirebaseUser().reload().addOnCompleteListener(task -> {

            if (task.isSuccessful() && FirebaseUserManager.getFirebaseUser().isEmailVerified()) {
                showMainScreen();
                return;
            }
            showEmailSentDialog();
            Log.e(getClass().getName(), "Email already sent please verify");

        });
    }

    private void startLoading() {
        circularProgressBar.setVisibility(View.VISIBLE);
        email.setEnabled(false);
        password.setEnabled(false);
        loginButton.setEnabled(false);
        facebookButton.setEnabled(false);
        googleButton.setEnabled(false);
        dontHaveAnAccountTextView.setEnabled(false);
        signUpTextView.setEnabled(false);
    }

    private void finishLoading() {
        circularProgressBar.setVisibility(View.INVISIBLE);
        email.setEnabled(true);
        password.setEnabled(true);
        loginButton.setEnabled(true);
        facebookButton.setEnabled(true);
        googleButton.setEnabled(true);
        dontHaveAnAccountTextView.setEnabled(true);
        signUpTextView.setEnabled(true);
    }

    private void handleTaskExceptionResults(Task<AuthResult> task) {
        try {
            throw task.getException();
        } catch (FirebaseNetworkException firebaseNetworkException) {
            showNoInternetDialog();
        } catch (FirebaseAuthInvalidUserException firebaseAuthInvalidUserException) {
            if (firebaseAuthInvalidUserException.getErrorCode().equals("ERROR_USER_DISABLED")) {
                CuteToast.ct(this, getString(getDisabledAccountMessage), Toast.LENGTH_LONG, 3, true).show();
                return;
            }
            CuteToast.ct(this, getString(getIncorrectEmailOrPasswordMessage), Toast.LENGTH_SHORT, 3, true).show();
        } catch (Exception e) {
            CuteToast.ct(this, e.getMessage(), Toast.LENGTH_SHORT, 3, true).show();
            Log.e("SignIn", e.getMessage().toUpperCase());
        }
    }

    private void initializeAttributes() {
        email = findViewById(editlogin_TextEmail);
        password = findViewById(editLogin_TextPassword);
        loginButton = findViewById(LogInButton);
        facebookButton = findViewById(FacebookButton);
        googleButton = findViewById(GoogleButton);
        dontHaveAnAccountTextView = findViewById(TextView_DontHaveAnAccount);
        signUpTextView = findViewById(TextViewSignUp);
        circularProgressBar = findViewById(LoadingProgressBar);
        toastMessageBackButton = new CustomToastMessage(this, getString(getDoubleTappedMessage), 10);
        noInternetDialog = new Dialog(this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
        emailSentDialog = new Dialog(this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);


    }

    private void showMainScreen() {
        startActivity(new Intent(this, MainScreen.class));
        finish();
    }

    private void showNoInternetDialog() {
        if (noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
            return;
        }
        noInternetDialog.show();

    }

    private void showEmailSentDialog() {

        if (emailSentDialog.isShowing()) {
            emailSentDialog.dismiss();
            return;
        }

        emailSentDialog.show();


    }


}