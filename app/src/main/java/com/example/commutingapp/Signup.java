package com.example.commutingapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.rejowan.cutetoast.CuteToast;

import FirebaseUserManager.FirebaseUserManager;
import InternetConnection.ConnectionManager;
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
import static com.example.commutingapp.R.layout.activity_signup;
import static com.example.commutingapp.R.layout.custom_emailsent_dialog;
import static com.example.commutingapp.R.layout.custom_no_internet_dialog;
import static com.example.commutingapp.R.string.getDoubleTappedMessage;
import static com.example.commutingapp.R.string.getSendingEmailErrorMessage;


public class Signup extends AppCompatActivity {

    private final long twoMinutes = 120000;
    private EditText email, password, confirmPassword;
    private Button back_Button, createButton;
    private TextView alreadyHaveAnAccount, loginHere;
    private ConnectionManager connectionManager;
    private ProgressBar circularProgressbar;
    private UserManager userManager;
    private Dialog noInternetDialog;
    private CustomToastMessage toastMessageBackButton;
    private CountDownTimer verificationTimer;


    private void initializeAttributes() {
        email = findViewById(editTextSignUpEmailAddress);
        password = findViewById(editTextSignUpPassword);
        confirmPassword = findViewById(editSignUpConfirmPassword);
        alreadyHaveAnAccount = findViewById(TextView_AlreadyHaveAccount);
        loginHere = findViewById(TextView_LoginHere);
        back_Button = findViewById(BackButton);
        createButton = findViewById(CreateButton);
        circularProgressbar = findViewById(LoadingProgressBar);
        noInternetDialog = new Dialog(this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);

        toastMessageBackButton = new CustomToastMessage(this, getString(getDoubleTappedMessage), 10);
    }

    @Override
    protected void onDestroy() {
        if (noInternetDialog.isShowing()) {
            noInternetDialog.dismiss();
        }
        if (verificationTimer != null) {
            verificationTimer.cancel();
        }
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
        userManager = new UserManager(getBaseContext(), email, password, confirmPassword);
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

    //TODO fix later
    public void resendEmailIsClicked(View view) {

                FirebaseUserManager.getFirebaseUser().sendEmailVerification().addOnCompleteListener(task -> {
                    //show cutetoast
                    if(task.isSuccessful()){
                        Log.e(getClass().getName(),"SUCCESS");
                        return;
                    }
                    if(!task.isSuccessful()){

                    }
                });


    }


    public void refreshButtonClicked(View view) {
        FirebaseUserManager.getFirebaseUser().reload().addOnCompleteListener(task -> {
            if (task.isSuccessful() && FirebaseUserManager.getFirebaseUser().isEmailVerified()) {
                showMainScreen();
            }
        });
    }


    private void startTimerForVerification() {
        verificationTimer = new CountDownTimer(twoMinutes, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e(getClass().getName(), "Ticking");
            }

            @Override
            public void onFinish() {

            }
        }.start();

    }

    private void signOutPreviousAccount() {
        FirebaseUserManager.getFirebaseAuth().signOut();
    }

    private boolean isUserCreatedNewAccount() {
        String userEmail = email.getText().toString().trim();
        return !FirebaseUserManager.getFirebaseUser().getEmail().equals(userEmail);
    }


    private void sendEmailVerificationToUser() {
        FirebaseUserManager.getFirebaseUser().sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                clearInputs();
                showEmailSentDialog();
                return;
            }
            //TODO changing UI
            CuteToast.ct(this, getString(getSendingEmailErrorMessage), Toast.LENGTH_LONG, 3, true).show();
        });
    }


    private void ProceedToSignUp() {
        String userEmail = email.getText().toString().trim();
        String userConfirmPassword = confirmPassword.getText().toString().trim();

        startLoading();
        FirebaseUserManager.getFirebaseAuth().createUserWithEmailAndPassword(userEmail, userConfirmPassword).addOnCompleteListener(task -> {

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

    private void handleTaskExceptionResults(Task<AuthResult> task) {
        try {
            throw task.getException();
        } catch (FirebaseNetworkException firebaseNetworkException) {
            showNoInternetDialog();
        } catch (Exception ex) {
            CuteToast.ct(this, task.getException().getMessage(), Toast.LENGTH_SHORT, 3, true).show();
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

    private void showEmailSentDialog() {
        setContentView(custom_emailsent_dialog);
        displayUsersEmailToTextView();
    }

    private void displayUsersEmailToTextView() {
        TextView emailTextView = findViewById(textViewEmail);
        String usersEmail = FirebaseUserManager.getFirebaseUser().getEmail();
        emailTextView.setText(usersEmail);
    }

}