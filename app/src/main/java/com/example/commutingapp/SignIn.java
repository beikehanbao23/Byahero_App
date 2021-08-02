package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
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
import java.util.Arrays;
import java.util.Objects;
import FirebaseUserManager.FirebaseUserManager;
import InternetConnection.ConnectionManager;
import Logger.CustomDialogs;
import Logger.CustomToastMessage;
import MenuButtons.BackButtonDoubleClicked;
import MenuButtons.CustomBackButton;
import Screen.ScreenDimension;
import ValidateUser.UserManager;
import static com.example.commutingapp.R.id.*;
import static com.example.commutingapp.R.layout.activity_sign_in;
import static com.example.commutingapp.R.string.*;


public class SignIn extends AppCompatActivity implements BackButtonDoubleClicked {

    private final String TAG = "FacebookAuthentication";
    private EditText email, password;
    private Button googleButton, loginButton, facebookButton;
    private TextView dontHaveAnAccountTextView, signUpTextView;
    private CustomToastMessage toastMessageBackButton;
    private ConnectionManager connectionManager;
    private ProgressBar circularProgressBar;
    private UserManager userManager;
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
        facebookButton = findViewById(FacebookButton);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        new ScreenDimension(getWindow()).windowToFullScreen();
        setContentView(activity_sign_in);
        initializeAttributes();
        FirebaseUserManager.initializeFirebase();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        removeFacebookPreviousToken();


    }

    public void FacebookButtonIsClicked(View view) {
        loginUsingFacebook();
    }

    public void loginUsingFacebook(){

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.e(TAG, "facebook:onSuccess:" + loginResult);
                        startLoading();
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG, "facebook:onCancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.e(TAG, "facebook:onError", error);

                        if(Objects.equals(error.getMessage(), "CONNECTION_FAILURE: CONNECTION_FAILURE")){
                            showNoInternetDialog();
                            return;
                        }

                        customPopupDialog.showErrorDialog("Error", Objects.requireNonNull(error.getMessage()));
                        removeFacebookPreviousToken();


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
                       finishLoading();
                        customPopupDialog.showErrorDialog("Error", "Authentication Failed.");

                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "Calling:: onActivityResult");
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void removeFacebookPreviousToken(){
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
            Log.e(TAG,"RemovingToken");
        }
    }


    @Override
    protected void onDestroy() {
        LoginManager.getInstance().unregisterCallback(callbackManager);
        super.onDestroy();
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

      startActivity(new Intent(this,NoInternet.class));
    }

    private void showEmailSentDialog() {

      startActivity(new Intent(this,EmailSent.class));
      finish();
    }

}