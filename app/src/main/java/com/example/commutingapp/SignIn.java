package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

import FirebaseUserManager.FirebaseManager;
import InternetConnection.ConnectionManager;
import Logger.CustomDialogs;
import Logger.CustomToastMessage;
import MenuButtons.CustomBackButton;
import UI.ActivitySwitcher;
import UI.AttributesInitializer;
import UI.BindingDestroyer;
import UI.LoadingScreen;
import UI.ScreenDimension;
import Users.UserManager;


import static com.example.commutingapp.R.string.disabledAccountMessage;
import static com.example.commutingapp.R.string.doubleTappedMessage;
import static com.example.commutingapp.R.string.incorrectEmailOrPasswordMessage;


public class SignIn extends AppCompatActivity implements LoadingScreen, BindingDestroyer, AttributesInitializer {

    private static final int RC_SIGN_IN = 123;
    private final String TAG = "FacebookAuthentication";
    private final String FACEBOOK_CONNECTION_FAILURE = "CONNECTION_FAILURE: CONNECTION_FAILURE";
    private CustomDialogs customPopupDialog;
    private CallbackManager facebookCallBackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private CustomToastMessage toastMessageBackButton;
    private ActivitySignInBinding activitySignInBinding;
    private CircularProgressbarBinding circularProgressbarBinding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        initializeAttributes();
        createRequestSignOptionsGoogle();
        FirebaseManager.initializeFirebaseApp();
        FacebookSdk.sdkInitialize(getApplicationContext());
        facebookCallBackManager = CallbackManager.Factory.create();

        removeFacebookUserAccountPreviousToken();

    }

    @Override public void initializeAttributes() {

        activitySignInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        circularProgressbarBinding = CircularProgressbarBinding.bind(activitySignInBinding.getRoot());
        new ScreenDimension(getWindow()).setWindowToFullScreen();
        setContentView(activitySignInBinding.getRoot());
        customPopupDialog = new CustomDialogs(this);
        toastMessageBackButton = new CustomToastMessage(this, getString(doubleTappedMessage), 10);

    }


    public void FacebookButtonIsClicked(View view) {
        loginViaFacebook();
    }
    public void loginViaFacebook() {

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
                handleFacebookSignInException(error);
            }
        });
    }


    private void handleFacebookSignInException(Exception error){

        if (Objects.equals(error.getMessage(), FACEBOOK_CONNECTION_FAILURE)) {
            showNoInternetActivity();
            return;
        }
        customPopupDialog.showErrorDialog("Error", error.getMessage());
        removeFacebookUserAccountPreviousToken();
    }


    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential authCredential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseManager.getFirebaseAuthInstance().signInWithCredential(authCredential).
                addOnCompleteListener(this,
                        task -> {
                            if (task.isSuccessful()) {
                                FirebaseManager.getCreatedUserAccount();
                                showMainScreenActivity();
                                return;
                            }
                            finishLoading();
                           handleFacebookSignInException(task.getException());
                        });
    }
    private void removeFacebookUserAccountPreviousToken() {
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
    }
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        handleFacebookSignInCallback(requestCode, resultCode, data);
        handleGoogleSignInCallback(requestCode, data);

    }
    private void handleFacebookSignInCallback(int requestCode,int resultCode, Intent data){
        facebookCallBackManager.onActivityResult(requestCode, resultCode, data);
    }
    private void handleGoogleSignInCallback(int requestCode, Intent data){
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                showLoading();
                authenticateFirebaseWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                handleGoogleSignInException(e);
            }
        }
    }
    private void handleGoogleSignInException(ApiException error){

        if(error.getStatus().isCanceled()){
            return;
        }
        if(!error.getStatus().isSuccess() && noInternetConnection()){
            showNoInternetActivity();
        }
        Log.e("This only happens ","in onCancel() event");
    }
    private boolean noInternetConnection(){
        return !new ConnectionManager(this).internetConnectionAvailable();
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
        FirebaseManager.getFirebaseAuthInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseManager.getCreatedUserAccount();
                        showMainScreenActivity();
                        return;
                    }
                    finishLoading();
                    customPopupDialog.showErrorDialog("Error", "Authentication Failed. Please try again later.");
                });
    }
    @Override protected void onDestroy() {
        LoginManager.getInstance().unregisterCallback(facebookCallBackManager);
        destroyBinding();
        super.onDestroy();
    }
    @Override public void destroyBinding(){
        activitySignInBinding = null;
        circularProgressbarBinding = null;
    }
    public void SignUpTextClicked(View view) {
        showSignUpActivity();
    }
    @Override protected void onStart() {
        super.onStart();

    }
    @Override public void onBackPressed() {
        new CustomBackButton(this,this).applyDoubleClickToExit();
    }
    public void SignInButtonIsClicked(View view) {
        loginViaDefaultSignIn();
    }
    private void loginViaDefaultSignIn(){
        UserManager userManager = new UserManager(this,
                activitySignInBinding.editloginTextEmail,
                activitySignInBinding.editLoginTextPassword,
                null);
        if (userManager.signInValidationFail()) {
            return;
        }

        if (noInternetConnection()) {
            showNoInternetActivity();
            return;
        }

        proceedToSignIn();
    }
    private void proceedToSignIn() {

        String email = activitySignInBinding.editloginTextEmail.getText().toString().trim();
        String userPassword = activitySignInBinding.editLoginTextPassword.getText().toString().trim();

        showLoading();
        FirebaseManager.getFirebaseAuthInstance().signInWithEmailAndPassword(
                email, userPassword).addOnCompleteListener(this, signInTask -> {
            if (signInTask.isSuccessful()) {
                FirebaseManager.getCreatedUserAccount();
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
        FirebaseManager.getFirebaseUserInstance().reload().addOnCompleteListener(emailReload -> {

            if (emailReload.isSuccessful() && FirebaseManager.getFirebaseUserInstance().isEmailVerified()) {
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
            showNoInternetErrorDialog(firebaseAuthInvalidUserException);
        } catch (Exception e) {
            customPopupDialog.showErrorDialog("Oops..", e.getMessage());
        }
    }
    private void showNoInternetErrorDialog(FirebaseAuthInvalidUserException firebaseAuthInvalidUserException){
        if (firebaseAuthInvalidUserException.getErrorCode().equals("ERROR_USER_DISABLED")) {
            customPopupDialog.showErrorDialog("Oops..", getString(disabledAccountMessage));
            return;
        }
        customPopupDialog.showErrorDialog("Oops..", getString(incorrectEmailOrPasswordMessage));
    }
    @Override public void showLoading() {
        makeLoading(false,View.VISIBLE);
    }
    @Override public void finishLoading() {
        makeLoading(true,View.INVISIBLE);
    }
    @Override public void makeLoading(boolean visible, int progressBarVisibility){
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

        ActivitySwitcher.INSTANCE.startActivityOf(this,this,MainScreen.class);
    }
    private void showNoInternetActivity() {

        ActivitySwitcher.INSTANCE.startActivityOf(this,NoInternet.class);
    }
    private void showEmailSentActivity() {

        ActivitySwitcher.INSTANCE.startActivityOf(this,this,EmailSent.class);
    }
    private void showSignUpActivity(){
        ActivitySwitcher.INSTANCE.startActivityOf(this,this,Signup.class);
    }
    public void googleButtonIsClicked(View view) {
        loginViaGoogle();
    }
    private void loginViaGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}