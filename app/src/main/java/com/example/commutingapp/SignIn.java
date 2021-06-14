package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;

import FirebaseUserManager.FirebaseUserManager;
import MenuButtons.BackButton;

public class SignIn extends AppCompatActivity {
    private BackButton backButton;
    private EditText email, password;
    private FirebaseUserManager firebaseUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        new splashscreen().finish();
        backButton = new BackButton(this.getBaseContext(), 2000, "Tap again to exit");
        email = findViewById(R.id.editlogin_TextName);
        password = findViewById(R.id.editlogin_TextPassword);
        firebaseUserManager = new FirebaseUserManager();
        firebaseUserManager.initializeFirebase();
    }


    public void SignUpTextClicked(View view) {
        this.startActivity(new Intent(this, Signup.class));
        finish();
    }

    @Override
    public void onBackPressed() {
       backButton.backButtonisPressed();
    }

    public void SignInButtonIsClicked(View view) {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        firebaseUserManager.loginUser(email,password);
        if(firebaseUserManager.UserInputRequirementsFailedAtSignIn()){
            return;
        }


        firebaseUserManager.getFirebaseAuthenticate().signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
            firebaseUserManager.getCurrentUser();
            startActivity(new Intent(this,MainScreen.class));
            return;
        }
            Toast.makeText(new Signup().getBaseContext(), "Failed to sign in account.", Toast.LENGTH_SHORT).show(); // change later
        });
    }
}