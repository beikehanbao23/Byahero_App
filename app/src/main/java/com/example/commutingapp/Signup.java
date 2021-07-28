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

import java.util.Objects;

import FirebaseUserManager.FirebaseUserManager;
import InternetConnection.ConnectionManager;
import Logger.CustomDialogs;
import Logger.CustomToastMessage;
import MenuButtons.CustomBackButton;
import MenuButtons.backButton;
import ValidateUser.UserManager;

import static com.example.commutingapp.R.id.BackButton;
import static com.example.commutingapp.R.id.CreateButton;
import static com.example.commutingapp.R.id.LoadingProgressBar;
import static com.example.commutingapp.R.id.TextView_AlreadyHaveAccount;
import static com.example.commutingapp.R.id.TextView_LoginHere;
import static com.example.commutingapp.R.id.editSignUpConfirmPassword;
import static com.example.commutingapp.R.id.editTextSignUpEmailAddress;
import static com.example.commutingapp.R.id.editTextSignUpPassword;
import static com.example.commutingapp.R.id.textViewEmail;
import static com.example.commutingapp.R.id.textViewResendEmail;
import static com.example.commutingapp.R.layout.activity_signup;
import static com.example.commutingapp.R.layout.custom_emailsent_dialog;
import static com.example.commutingapp.R.layout.custom_no_internet_dialog;
import static com.example.commutingapp.R.string.doubleTappedMessage;
import static com.example.commutingapp.R.string.resendEmailFailedMessage;
import static com.example.commutingapp.R.string.resendEmailSuccessMessage;
import static com.example.commutingapp.R.string.sendingEmailErrorMessage;


public class Signup extends AppCompatActivity {

    private final long twoMinutes = 120000;
    private final long oneSecondInMillis = 1000;
    private EditText email, password, confirmPassword;
    private Button back_Button, createButton;
    private TextView alreadyHaveAnAccount, loginHere, resendEmailTextView;
    private ConnectionManager connectionManager;
    private ProgressBar circularProgressbar, circularProgressBarForEmailSentDialog;
    private Dialog noInternetDialog;
    private CustomToastMessage toastMessageBackButton;
    private CountDownTimer verificationTimer;
    private CustomDialogs customPopupDialog;

    private void initializeAttributes() {
        email = findViewById(editTextSignUpEmailAddress);
        password = findViewById(editTextSignUpPassword);
        confirmPassword = findViewById(editSignUpConfirmPassword);
        alreadyHaveAnAccount = findViewById(TextView_AlreadyHaveAccount);
        loginHere = findViewById(TextView_LoginHere);
        back_Button = findViewById(BackButton);
        createButton = findViewById(CreateButton);
        circularProgressbar = findViewById(LoadingProgressBar);
        customPopupDialog = new CustomDialogs(this);
        noInternetDialog = new Dialog(this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
        toastMessageBackButton = new CustomToastMessage(this, getString(doubleTappedMessage), 10);
    }

    @Override
    protected void onDestroy() {
        closeInternetDialog();
        removeVerificationTimer();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(activity_signup);

        initializeAttributes();

        noInternetDialog.setContentView(custom_no_internet_dialog);
        FirebaseUserManager.initializeFirebase();

    }


    @Override
    public void onBackPressed() {

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


    @Override
    protected void onStart() {
        super.onStart();
        connectionManager = new ConnectionManager(this);
    }

    public void backToSignInButton(View view) {
        startActivity(new Intent(this, SignIn.class));
        finish();
    }


    public void CreateButtonClicked(View view) {
        UserManager userManager = new UserManager(getBaseContext(), email, password, confirmPassword);
        if (userManager.UserInputRequirementsFailedAtSignUp()) {
            return;
        }
        if (!connectionManager.PhoneHasInternetConnection()) {
            showNoInternetDialog();
            return;
        }
        FirebaseUserManager.getCurrentUser();
        if (FirebaseUserManager.isUserAlreadySignedIn() && isUserCreatedNewAccount()) {
            signOutPreviousAccount();
            ProceedToSignUp();
            return;
        }
        ProceedToSignUp();
    }


    public void retryButtonClicked(View view) {
        if (connectionManager.PhoneHasInternetConnection() && noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }
    }

    public void GoToSettingsClicked(View view) {
        startActivity(new Intent(Settings.ACTION_SETTINGS));
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


    public void refreshButtonClicked(View view) {
        circularProgressBarForEmailSentDialog = findViewById(LoadingProgressBar);
        circularProgressBarForEmailSentDialog.setVisibility(View.VISIBLE);
        FirebaseUserManager.getFirebaseUserInstance().reload().addOnCompleteListener(task -> {
            if (task.isSuccessful() && FirebaseUserManager.getFirebaseUserInstance().isEmailVerified()) {
                showMainScreen();
            }
            if (task.getException() != null) {
                handleTaskExceptionResults(task);
            }
            circularProgressBarForEmailSentDialog.setVisibility(View.INVISIBLE);
        });
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

    private void setDisplayForResendEmailTextViewWhile_TimerOnTick(long secondsLeft) {

        resendEmailTextView.setTextColor(ContextCompat.getColor(this, R.color.gray));
        resendEmailTextView.setText("Resend verification in " + secondsLeft + "s");
        resendEmailTextView.setEnabled(false);
    }

    private void setDisplayForResendEmailTextWhenTimerFinished() {
        resendEmailTextView.setTextColor(ContextCompat.getColor(this, R.color.blue2));
        resendEmailTextView.setText("Resend verification");
        resendEmailTextView.setEnabled(true);
    }

    private void startTimerForVerification() {
        resendEmailTextView = findViewById(textViewResendEmail);
        verificationTimer = new CountDownTimer(twoMinutes, oneSecondInMillis) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / oneSecondInMillis;
                setDisplayForResendEmailTextViewWhile_TimerOnTick(secondsLeft);
            }

            @Override
            public void onFinish() {
                setDisplayForResendEmailTextWhenTimerFinished();
            }
        }.start();

    }

    private void signOutPreviousAccount() {

        FirebaseUserManager.getFirebaseAuthInstance().signOut();
    }

    private boolean isUserCreatedNewAccount() {
        String userEmail = email.getText().toString().trim();
        return !Objects.equals(FirebaseUserManager.getFirebaseUserInstance().getEmail(), userEmail);
    }


    private void sendEmailVerificationToUser() {
        FirebaseUserManager.getFirebaseUserInstance().sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                clearInputs();
                setEmailSentDialog();
                return;
            }
            customPopupDialog.showErrorDialog("Error", getString(sendingEmailErrorMessage));
        });
    }


    private void ProceedToSignUp() {
        String userEmail = email.getText().toString().trim();
        String userConfirmPassword = confirmPassword.getText().toString().trim();

        startLoading();
        FirebaseUserManager.getFirebaseAuthInstance().createUserWithEmailAndPassword(userEmail, userConfirmPassword).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                FirebaseUserManager.getCurrentUser();
                finishLoading();
                sendEmailVerificationToUser();

                return;

            }

            if (task.getException() != null) {
                finishLoading();
                handleTaskExceptionResults(task);
            }

        });
    }

    private void handleTaskExceptionResults(Task<?> task) {
        try {
            throw task.getException();
        } catch (FirebaseNetworkException firebaseNetworkException) {
            showNoInternetDialog();
        } catch (Exception ex) {
            customPopupDialog.showErrorDialog("Error", task.getException().getMessage());
        }
    }

    private void startLoading() {
        circularProgressbar.setVisibility(View.VISIBLE);
        email.setEnabled(false);
        password.setEnabled(false);
        confirmPassword.setEnabled(false);
        alreadyHaveAnAccount.setEnabled(false);
        loginHere.setEnabled(false);
        back_Button.setEnabled(false);
        createButton.setEnabled(false);

    }

    private void clearInputs() {
        email.setText("");
        password.setText("");
        confirmPassword.setText("");
    }

    private void finishLoading() {
        circularProgressbar.setVisibility(View.INVISIBLE);
        email.setEnabled(true);
        password.setEnabled(true);
        confirmPassword.setEnabled(true);
        alreadyHaveAnAccount.setEnabled(true);
        loginHere.setEnabled(true);
        back_Button.setEnabled(true);
        createButton.setEnabled(true);
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

    private void displayUsersEmailToTextView() {
        TextView emailTextView = findViewById(textViewEmail);
        String usersEmail = FirebaseUserManager.getFirebaseUserInstance().getEmail();
        emailTextView.setText(usersEmail);
    }

    
}