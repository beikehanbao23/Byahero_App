package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.rejowan.cutetoast.CuteToast;

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

        backButton.showToastMessageThenBack();
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
            finish();
            return;
        }
            CuteToast.ct(this, "Username or password is incorrect", CuteToast.LENGTH_SHORT, CuteToast.ERROR, true).show(); // change later
        });
    }
}