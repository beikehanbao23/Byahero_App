package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import FirebaseUserManager.FirebaseUserRegister;


public class Signup extends AppCompatActivity {

    private EditText name, email, phoneNumber, password, confirmPassword;
    private FirebaseUserRegister firebaseUserRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.EditTextName);
        email = findViewById(R.id.EditTextEmail);
        phoneNumber = findViewById(R.id.editTextPhone);
        password = findViewById(R.id.editTextPassword);
        confirmPassword = findViewById(R.id.EditTextConfirmPassword);
        firebaseUserRegister = new FirebaseUserRegister();
        firebaseUserRegister.initializeFirebase();


    }


    public void backToSignInButton(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();

    }

    @Override
    public void onBackPressed() {
        backToSignInButton(null);
    }


    public void CreateBttnClicked(View view) {
    firebaseUserRegister.createUser(name,email,phoneNumber,password,confirmPassword);

    if(firebaseUserRegister.UserInputRequirementsFailed()){
        return;
    }
    firebaseUserRegister.getFirebaseAuthenticate().createUserWithEmailAndPassword(firebaseUserRegister.getEmail(),firebaseUserRegister.getConfirmPassword()).addOnCompleteListener(task -> {
        if(task.isSuccessful()){
            firebaseUserRegister.getCurrentUser();
            startActivity(new Intent(this,MainScreen.class));
            return;
        }
        Toast.makeText(new Signup().getBaseContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
    });

    }


}