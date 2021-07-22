package com.example.commutingapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import FirebaseUserManager.FirebaseUserManager;
import InternetConnection.ConnectionManager;
import Logger.CustomToastMessage;
import MenuButtons.BackButtonDoubleClicked;
import MenuButtons.CustomBackButton;
import ValidateUser.UserManager;
import id.ionbit.ionalert.IonAlert;

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
    private EditText email, password;
    private Button facebookButton, googleButton, loginButton;
    private Dialog noInternetDialog;
    private TextView dontHaveAnAccountTextView, signUpTextView, resendEmailTextView;
    private CustomToastMessage toastMessageBackButton;
    private ConnectionManager connectionManager;
    private ProgressBar circularProgressBar, circularProgressBarEmailSent;
    private UserManager userManager;
    private CountDownTimer verificationTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(activity_sign_in);
        initializeAttributes();

        FirebaseUserManager.initializeFirebase();
        noInternetDialog.setContentView(custom_no_internet_dialog);


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
        FirebaseUserManager.getFirebaseUser().reload().addOnCompleteListener(task -> {
            if (task.isSuccessful() && FirebaseUserManager.getFirebaseUser().isEmailVerified()) {
                showMainScreen();
            }
            if (task.getException() != null) {
                handleTaskExceptionResults(task);
            }
            circularProgressBarEmailSent.setVisibility(View.INVISIBLE);
        });
    }


    public void resendEmailIsClicked(View view) {

        FirebaseUserManager.getFirebaseUser().sendEmailVerification().addOnCompleteListener(task -> {
            startTimerForVerification();
            if (task.isSuccessful()) {
                showSuccessDialog("New email sent", getString(resendEmailSuccessMessage));
                return;
            }
            showWarningDialog("Please check your inbox", getString(resendEmailFailedMessage));
        });
    }


    private void removeVerificationTimer() {
        if (verificationTimer != null) {
            verificationTimer.cancel();
        }
        System.out.println();
    }

    private void closeInternetDialog() {
        if (noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }
    }

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
            setEmailSentDialog();


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
                showErrorDialog("Oops..", getString(disabledAccountMessage));
                return;
            }
            showErrorDialog("Oops..", getString(incorrectEmailOrPasswordMessage));
        } catch (Exception e) {
            showErrorDialog("Oops..", e.getMessage());
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

        toastMessageBackButton = new CustomToastMessage(this, getString(doubleTappedMessage), 10);
        noInternetDialog = new Dialog(this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);


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

    private void setEmailSentDialog() {

        setContentView(custom_emailsent_dialog);
        displayUsersEmailToTextView();

    }
    //TODO("Create class for this")
    private IonAlert customDialog(String title, String contextText, int type) {
        IonAlert alertDialog = new IonAlert(this, type);
        alertDialog.setTitleText(title);
        alertDialog.setContentText(contextText);
        return alertDialog;
    }

    private void showErrorDialog(String title, String contextText) {
        customDialog(title, contextText, IonAlert.ERROR_TYPE).show();
    }

    private void showSuccessDialog(String title, String contentText) {
        customDialog(title, contentText, IonAlert.SUCCESS_TYPE).show();
    }

    private void showWarningDialog(String title, String contentText) {
        customDialog(title, contentText, IonAlert.WARNING_TYPE).show();
    }


    private void displayUsersEmailToTextView() {
        TextView emailTextView = findViewById(textViewEmail);
        String usersEmail = FirebaseUserManager.getFirebaseUser().getEmail();
        emailTextView.setText(usersEmail);
    }


}