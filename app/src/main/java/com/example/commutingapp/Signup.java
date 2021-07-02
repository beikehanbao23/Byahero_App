package com.example.commutingapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
import ValidateUser.UserManager;

import static com.example.commutingapp.R.id.BackButton;
import static com.example.commutingapp.R.id.CreateButton;
import static com.example.commutingapp.R.id.LoadingProgressBar;
import static com.example.commutingapp.R.id.TextView_AlreadyHaveAccount;
import static com.example.commutingapp.R.id.TextView_LoginHere;
import static com.example.commutingapp.R.id.editSignUpConfirmPassword;
import static com.example.commutingapp.R.id.editTextSignUpEmailAddress;
import static com.example.commutingapp.R.id.editTextSignUpPassword;
import static com.example.commutingapp.R.layout.activity_signup;
import static com.example.commutingapp.R.layout.custom_emailsent_dialog;
import static com.example.commutingapp.R.layout.custom_no_internet_dialog;
import static com.example.commutingapp.R.string.getSendingEmailErrorMessage;

public class Signup extends AppCompatActivity {

    private EditText email, password, confirmPassword;
    private Button backButton, createButton;
    private TextView alreadyHaveAnAccount, loginHere;
    private ConnectionManager connectionManager;
    private ProgressBar circularProgressbar;
    private UserManager userManager;
    private Dialog noInternetDialog, emailSentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(activity_signup);

        initializeAttributes();

        emailSentDialog = new Dialog(this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
        noInternetDialog = new Dialog(this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
        noInternetDialog.setContentView(custom_no_internet_dialog);
        emailSentDialog.setContentView(custom_emailsent_dialog);

        FirebaseUserManager.initializeFirebase();


    }

    private void showNoInternetDialog() {
        noInternetDialog.show();
    }

    private void showEmailSentDialog() {
        emailSentDialog.show();
    }

    @Override
    public void onBackPressed() {
        backToSignInButton(null);
    }


    @Override
    protected void onStart() {
        super.onStart();
        connectionManager = new ConnectionManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    /*
    TODO:
    when user back to the application after verifying email then it automatically go to
    main screen without clicking the create button again.
     */
    }

    /*
    TODO recheck error message sign in
     */

    private void initializeAttributes() {
        email = findViewById(editTextSignUpEmailAddress);
        password = findViewById(editTextSignUpPassword);
        confirmPassword = findViewById(editSignUpConfirmPassword);
        alreadyHaveAnAccount = findViewById(TextView_AlreadyHaveAccount);
        loginHere = findViewById(TextView_LoginHere);
        backButton = findViewById(BackButton);
        createButton = findViewById(CreateButton);
        circularProgressbar = findViewById(LoadingProgressBar);
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
        if (FirebaseUserManager.isUserAlreadySignedIn()) {

            if (isUserCreatedNewAccount()) {
                signOutPreviousAccount();
                ProceedToSignUp();
                return;
            }

            SignupAndVerifyUserEmail();
            return;
        }

        ProceedToSignUp();


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
                showEmailSentDialog();
                return;
            }
            //TODO changing UI
            CuteToast.ct(this, getString(getSendingEmailErrorMessage), Toast.LENGTH_LONG, 3, true).show();
        });
    }

    //TODO REFACTOR This
    private void SignupAndVerifyUserEmail() {

        try {
            //can't put the showEmailSentDialog inside while loop, the documentation says show method should not be override

            if (!FirebaseUserManager.getFirebaseUser().isEmailVerified()) {
                showEmailSentDialog();
                while (!FirebaseUserManager.getFirebaseUser().isEmailVerified()) {
                    FirebaseUserManager.getFirebaseUser().reload();
                    if (FirebaseUserManager.getFirebaseUser().isEmailVerified()) {
                        showMainScreen();
                        break;
                    }
                }
                return;
            }
            showMainScreen();

        } catch (Exception e) {
            CuteToast.ct(this, e.getMessage(), Toast.LENGTH_SHORT, 3, true).show();
        }


    }

    private void ProceedToSignUp() {
        String userEmail = email.getText().toString().trim();
        String userConfirmPassword = confirmPassword.getText().toString().trim();

        startLoading();
        FirebaseUserManager.getFirebaseAuth().createUserWithEmailAndPassword(userEmail, userConfirmPassword).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                FirebaseUserManager.getCurrentUser();
                sendEmailVerificationToUser();
                finishLoading();
                return;

            }

            if (task.getException() != null) {
                finishLoading();
                Log.e("Signup", "GOTCHA! ERROR AT SIGNUP USER FUNCTION");
                handleTaskExceptionResults(task);
            }

        });
    }

    public void handleTaskExceptionResults(Task<AuthResult> task) {
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
        backButton.setEnabled(false);
        createButton.setEnabled(false);

    }

    private void finishLoading() {
        circularProgressbar.setVisibility(View.INVISIBLE);
        email.setEnabled(true);
        password.setEnabled(true);
        confirmPassword.setEnabled(true);
        alreadyHaveAnAccount.setEnabled(true);
        loginHere.setEnabled(true);
        backButton.setEnabled(true);
        createButton.setEnabled(true);
    }

    private void showMainScreen() {

        startActivity(new Intent(this, MainScreen.class));
        finish();
    }

}