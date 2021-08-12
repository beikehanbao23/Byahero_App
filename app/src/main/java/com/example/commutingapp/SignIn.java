package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.commutingapp.databinding.ActivitySignInBinding;
import com.example.commutingapp.databinding.CircularProgressbarBinding;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.GoogleAuthProvider;

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


import static com.example.commutingapp.R.string.disabledAccountMessage;
import static com.example.commutingapp.R.string.doubleTappedMessage;
import static com.example.commutingapp.R.string.incorrectEmailOrPasswordMessage;


public class SignIn extends AppCompatActivity implements BackButtonDoubleClicked {

    private static final int RC_SIGN_IN = 123;
    private final String TAG = "FacebookAuthentication";
    private final String FACEBOOK_CONNECTION_FAILURE = "CONNECTION_FAILURE: CONNECTION_FAILURE";
    private CustomDialogs customPopupDialog;
    private CallbackManager facebookCallBackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private CustomToastMessage toastMessageBackButton;
    private ActivitySignInBinding activitySignInBinding;
    private CircularProgressbarBinding circularProgressbarBinding;

    private void initializeAttributes() {
        customPopupDialog = new CustomDialogs(this);
        toastMessageBackButton = new CustomToastMessage(this, getString(doubleTappedMessage), 10);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        activitySignInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        circularProgressbarBinding = CircularProgressbarBinding.bind(activitySignInBinding.getRoot());
        new ScreenDimension(getWindow()).windowToFullScreen();

        setContentView(activitySignInBinding.getRoot());

        initializeAttributes();
        createRequestSignOptionsGoogle();
        FirebaseUserManager.initializeFirebase();
        FacebookSdk.sdkInitialize(getApplicationContext());
        facebookCallBackManager = CallbackManager.Factory.create();

        removeFacebookPreviousToken();


    }

    public void FacebookButtonIsClicked(View view) {
        loginUsingFacebook();
    }

    public void loginUsingFacebook() {

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(facebookCallBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                showLoading();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                handleFacebookException(error);
            }
        });
    }
    private void handleFacebookException(Exception error){

        if (Objects.equals(error.getMessage(), FACEBOOK_CONNECTION_FAILURE)) {
            showNoInternetActivity();
            return;
        }
        customPopupDialog.showErrorDialog("Error", error.getMessage());
        removeFacebookPreviousToken();
    }
    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential authCredential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseUserManager.getFirebaseAuthInstance().signInWithCredential(authCredential).
                addOnCompleteListener(this,
                        task -> {
                            if (task.isSuccessful()) {
                                FirebaseUserManager.getCurrentUser();
                                showMainScreenActivity();
                                return;
                            }
                            finishLoading();
                           handleFacebookException(task.getException());
                        });
    }
    private void handleFacebookSignInCallback(int requestCode,int resultCode, Intent data){
        facebookCallBackManager.onActivityResult(requestCode, resultCode, data);
    }
    private void removeFacebookPreviousToken() {
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        handleFacebookSignInCallback(requestCode, resultCode, data);
        handleGoogleSignInCallback(requestCode, data);

    }
    private void loginUsingGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void handleGoogleSignInCallback(int requestCode, Intent data){
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                showLoading();
                authenticateFirebaseWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                handleGoogleException(e);
            }
        }
    }
    private void handleGoogleException(ApiException error){

        if(error.getStatus().isCanceled()){
            return;
        }
        if(!error.getStatus().isSuccess() && !new ConnectionManager(this).PhoneHasInternetConnection()){
            showNoInternetActivity();
        }

    }

    private void createRequestSignOptionsGoogle() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }
    private void authenticateFirebaseWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseUserManager.getFirebaseAuthInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUserManager.getCurrentUser();
                        showMainScreenActivity();
                        return;
                    }
                    finishLoading();
                    customPopupDialog.showErrorDialog("Error", "Authentication Failed."); //TODO Change later
                });
    }




    @Override
    protected void onDestroy() {
        LoginManager.getInstance().unregisterCallback(facebookCallBackManager);
        activitySignInBinding = null;
        circularProgressbarBinding = null;
        super.onDestroy();
    }
    public void SignUpTextClicked(View view) {
        this.startActivity(new Intent(this, Signup.class));
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();

    }
    @Override
    public void onBackPressed() {
        backButtonClicked();
    }
    public void SignInButtonIsClicked(View view) {

        UserManager userManager = new UserManager(this,
                activitySignInBinding.editloginTextEmail,
                activitySignInBinding.editLoginTextPassword,
                null);
        if (userManager.userInputRequirementsFailedAtSignIn()) {
            return;
        }

        if (!new ConnectionManager(this).PhoneHasInternetConnection()) {
            showNoInternetActivity();
            return;
        }

        loginUsingDefaultSignIn();
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

    private void loginUsingDefaultSignIn() {

        String email = activitySignInBinding.editloginTextEmail.getText().toString().trim();
        String userPassword = activitySignInBinding.editLoginTextPassword.getText().toString().trim();

        showLoading();
        FirebaseUserManager.getFirebaseAuthInstance().signInWithEmailAndPassword(
                email, userPassword).addOnCompleteListener(this, signInTask -> {
            if (signInTask.isSuccessful()) {
                FirebaseUserManager.getCurrentUser();
                verifyUserEmail();
                return;
            }
            if (signInTask.getException() != null) {
                handleFirebaseSignInException(signInTask);
                finishLoading();
            }
        });

    }
    private void verifyUserEmail() {
        FirebaseUserManager.getFirebaseUserInstance().reload().addOnCompleteListener(emailReload -> {

            if (emailReload.isSuccessful() && FirebaseUserManager.getFirebaseUserInstance().isEmailVerified()) {
                showMainScreenActivity();
                return;
            }
            showEmailSentActivity();

        });
    }
    private void handleFirebaseSignInException(Task<?> task) {
        try {
            throw task.getException();
        } catch (FirebaseNetworkException firebaseNetworkException) {
            showNoInternetActivity();
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


    private void showLoading() {
        setLoading(false,View.VISIBLE);
    }
    private void finishLoading() {
        setLoading(true,View.INVISIBLE);
    }
    private void setLoading(boolean visible, int progressBarVisibility){
        circularProgressbarBinding.circularProgressBar.setVisibility(progressBarVisibility);
        activitySignInBinding.editloginTextEmail.setEnabled(visible);
        activitySignInBinding.editLoginTextPassword.setEnabled(visible);
        activitySignInBinding.LogInButton.setEnabled(visible);
        activitySignInBinding.FacebookButton.setEnabled(visible);
        activitySignInBinding.GoogleButton.setEnabled(visible);
        activitySignInBinding.TextViewDontHaveAnAccount.setEnabled(visible);
        activitySignInBinding.TextViewSignUp.setEnabled(visible);
    }



    private void showMainScreenActivity() {
        startActivity(new Intent(this, MainScreen.class));
        finish();
    }
    private void showNoInternetActivity() {

        startActivity(new Intent(this, NoInternet.class));
    }
    private void showEmailSentActivity() {

        startActivity(new Intent(this, EmailSent.class));
        finish();
    }
    public void googleButtonIsClicked(View view) {
        loginUsingGoogle();
    }


}