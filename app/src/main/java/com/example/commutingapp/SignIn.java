package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import com.rejowan.cutetoast.CuteToast;

import FirebaseUserManager.FirebaseUserManager;
import Logger.CustomToastMessage;
import MenuButtons.Clicks_BackButton;

public class SignIn extends AppCompatActivity {
    private Clicks_BackButton backButton;
    private EditText username, password;
    private FirebaseUserManager firebaseUserManager;
    private CustomToastMessage customToastMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        backButton = new Clicks_BackButton(this.getBaseContext(), 2000, "Tap again to exit");
        username = findViewById(R.id.editlogin_TextName);
        password = findViewById(R.id.editlogin_TextPassword);
        firebaseUserManager = new FirebaseUserManager();
        firebaseUserManager.initializeFirebase();
        customToastMessage = new CustomToastMessage(this,"Username or password is incorrect",3);
    }


    public void SignUpTextClicked(View view) {
        this.startActivity(new Intent(this, Signup.class));
        finish();
    }


    @Override
    public void onBackPressed() {

        backButton.showToastMessageThenBack();
    }

    public void SignInButtonIsClicked(View view) {
        String userEmail = username.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        firebaseUserManager.loginUser(username,password);
        if(firebaseUserManager.UserInputRequirementsFailedAtSignIn()){
            return;
        }


        firebaseUserManager.getFirebaseAuthenticate().signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
            firebaseUserManager.getCurrentUser();
            customToastMessage.hideMessage();
            startActivity(new Intent(this,MainScreen.class));
            finish();
            return;
        }
            customToastMessage.showMessage();
        });
    }
}