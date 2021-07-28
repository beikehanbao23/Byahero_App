package com.example.commutingapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import FirebaseUserManager.FirebaseUserManager;
import InternetConnection.ConnectionManager;
import Logger.CustomDialogs;
import Logger.CustomToastMessage;
import MenuButtons.BackButtonDoubleClicked;
import MenuButtons.CustomBackButton;
import ValidateUser.UserManager;

import static com.example.commutingapp.R.id.FacebookButton;
import static com.example.commutingapp.R.id.GoogleButton;
import static com.example.commutingapp.R.id.LoadingProgressBar;
import static com.example.commutingapp.R.id.LogInButton;
import static com.example.commutingapp.R.id.TextViewSignUp;
import static com.example.commutingapp.R.id.TextView_DontHaveAnAccount;
import static com.example.commutingapp.R.id.editLogin_TextPassword;
import static com.example.commutingapp.R.id.editlogin_TextEmail;
import static com.example.commutingapp.R.id.textViewEmail;
import static com.example.commutingapp.R.id.textViewResendEmail;
import static com.example.commutingapp.R.layout.activity_sign_in;
import static com.example.commutingapp.R.layout.custom_emailsent_dialog;
import static com.example.commutingapp.R.layout.custom_no_internet_dialog;
import static com.example.commutingapp.R.string.disabledAccountMessage;
import static com.example.commutingapp.R.string.doubleTappedMessage;
import static com.example.commutingapp.R.string.incorrectEmailOrPasswordMessage;
import static com.example.commutingapp.R.string.resendEmailFailedMessage;
import static com.example.commutingapp.R.string.resendEmailSuccessMessage;


public class SignIn extends AppCompatActivity implements BackButtonDoubleClicked {

    private final long twoMinutes = 120000;
    private final String TAG = "FacebookAuthentication";
    private EditText email, password;
    private Button googleButton, loginButton, facebookButton;
    private Dialog noInternetDialog;
    private TextView dontHaveAnAccountTextView, signUpTextView, resendEmailTextView;
    private CustomToastMessage toastMessageBackButton;
    private ConnectionManager connectionManager;
    private ProgressBar circularProgressBar, circularProgressBarEmailSent;
    private UserManager userManager;
    private CountDownTimer verificationTimer;
    private CustomDialogs customPopupDialog;
    private CallbackManager callbackManager;

    private void initializeAttributes() {
        email = findViewById(editlogin_TextEmail);
        password = findViewById(editLogin_TextPassword);
        loginButton = findViewById(LogInButton);
        googleButton = findViewById(GoogleButton);
        dontHaveAnAccountTextView = findViewById(TextView_DontHaveAnAccount);
        signUpTextView = findViewById(TextViewSignUp);
        circularProgressBar = findViewById(LoadingProgressBar);
        customPopupDialog = new CustomDialogs(this);
        toastMessageBackButton = new CustomToastMessage(this, getString(doubleTappedMessage), 10);
        noInternetDialog = new Dialog(this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
        facebookButton = findViewById(FacebookButton);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(activity_sign_in);
        initializeAttributes();
        FirebaseUserManager.initializeFirebase();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        noInternetDialog.setContentView(custom_no_internet_dialog);
        removePreviousToken();


    }

    public void FacebookButtonIsClicked(View view) {
        //TODO refactor

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.e(TAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG, "facebook:onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.e(TAG, "facebook:onError", error);
                        if(error instanceof FacebookAuthorizationException){
                            removePreviousToken();
                            FacebookButtonIsClicked(null);
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.e(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential authCredential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseUserManager.getFirebaseAuthInstance().signInWithCredential(authCredential).addOnCompleteListener(this,
                task -> {
                    if (task.isSuccessful()) {
                        Log.e(TAG, "signInWithCredential:success");
                        FirebaseUserManager.getCurrentUser();
                        showMainScreen();
                        return;
                    }
                    Log.e(TAG, "signInWithCredential:failure", task.getException());
                    customPopupDialog.showErrorDialog("Error", "Authentication Failed.");
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "Calling:: onActivityResult");
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void removePreviousToken(){
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
            Log.e(TAG,"RemovingToken");
        }
    }


    @Override
    protected void onDestroy() {
        closeInternetDialog();
        removeVerificationTimer();

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


    @Override
    public void backButtonClicked() {

        new CustomBackButton(() -> {
            if (MenuButtons.backButton.isDoubleTapped()) {
                toastMessageBackButton.hideToast();
                super.onBackPressed();
                return;
            }
            toastMessageBackButton.showToast();
            MenuButtons.backButton.registerFirstClick();
        }).backButtonIsClicked();
    }


    public void refreshButtonClicked(View view) {

        circularProgressBarEmailSent = findViewById(LoadingProgressBar);
        circularProgressBarEmailSent.setVisibility(View.VISIBLE);
        FirebaseUserManager.getFirebaseUserInstance().reload().addOnCompleteListener(emailReload -> {
            if (emailReload.isSuccessful() && FirebaseUserManager.getFirebaseUserInstance().isEmailVerified()) {
                showMainScreen();
            }
            if (emailReload.getException() != null) {
                handleTaskExceptionResults(emailReload);
            }
            circularProgressBarEmailSent.setVisibility(View.INVISIBLE);
        });
    }


    public void resendEmailIsClicked(View view) {

        FirebaseUserManager.getFirebaseUserInstance().sendEmailVerification().addOnCompleteListener(task -> {
            startTimerForVerification();
            if (task.isSuccessful()) {
                customPopupDialog.showSuccessDialog("New email sent", getString(resendEmailSuccessMessage));
                return;
            }
            customPopupDialog.showWarningDialog("Please check your inbox", getString(resendEmailFailedMessage));
        });
    }

    public void facebookButtonClicked(View view) {

    }


    private void removeVerificationTimer() {
        if (verificationTimer != null) {
            verificationTimer.cancel();
        }

    }

    private void closeInternetDialog() {
        if (noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }
    }
    //TODO FIX THIS
    private void setDisplayForResendEmailTextViewToNotClickable(long secondsLeft) {
        resendEmailTextView.setTextColor(ContextCompat.getColor(this, R.color.gray));
        resendEmailTextView.setText("Resend verification in " + secondsLeft + "s");
        resendEmailTextView.setEnabled(false);
    }

    private void setDisplayForResendEmailTextToDefault() {
        resendEmailTextView.setTextColor(ContextCompat.getColor(this, R.color.blue2));
        resendEmailTextView.setText("Resend verification");
        resendEmailTextView.setEnabled(true);
    }


    private void startTimerForVerification() {
        resendEmailTextView = findViewById(textViewResendEmail);
        verificationTimer = new CountDownTimer(twoMinutes, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                setDisplayForResendEmailTextViewToNotClickable(secondsLeft);
            }

            @Override
            public void onFinish() {
                setDisplayForResendEmailTextToDefault();
            }
        }.start();

    }


    private void ProceedToLogin() {

        String userUsername = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        startLoading();
        FirebaseUserManager.getFirebaseAuthInstance().signInWithEmailAndPassword(userUsername, userPassword).addOnCompleteListener(this, signInTask -> {
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
        FirebaseUserManager.getFirebaseUserInstance().reload().addOnCompleteListener(emailReload -> {

            if (emailReload.isSuccessful() && FirebaseUserManager.getFirebaseUserInstance().isEmailVerified()) {
                showMainScreen();
                return;
            }
            showEmailSentDialog();

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


    private void handleTaskExceptionResults(Task<?> task) {
        try {
            throw task.getException();
        } catch (FirebaseNetworkException firebaseNetworkException) {
            showNoInternetDialog();
        } catch (FirebaseAuthInvalidUserException firebaseAuthInvalidUserException) {
            if (firebaseAuthInvalidUserException.getErrorCode().equals("ERROR_USER_DISABLED")) {
                customPopupDialog.showErrorDialog("Oops..", getString(disabledAccountMessage));
                return;
            }
            customPopupDialog.showErrorDialog("Oops..", getString(incorrectEmailOrPasswordMessage));
        } catch (Exception e) {
            customPopupDialog.showErrorDialog("Oops..", e.getMessage());
        }
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

        setContentView(custom_emailsent_dialog);
        displayUsersEmailToTextView();

    }

    private void displayUsersEmailToTextView() {
        TextView emailTextView = findViewById(textViewEmail);
        String usersEmail = FirebaseUserManager.getFirebaseUserInstance().getEmail();
        emailTextView.setText(usersEmail);
    }


}