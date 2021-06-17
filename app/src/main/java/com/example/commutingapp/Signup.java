package com.example.commutingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rejowan.cutetoast.CuteToast;

import FirebaseUserManager.FirebaseUserManager;
import Logger.CustomToastMessage;


public class Signup extends AppCompatActivity {

    private EditText name, email, phoneNumber, password, confirmPassword;
    private FirebaseUserManager firebaseUserManager;
    private CustomToastMessage toastMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.EditTextName);
        email = findViewById(R.id.EditTextEmail);
        phoneNumber = findViewById(R.id.editTextPhone);
        password = findViewById(R.id.editTextPassword);
        confirmPassword = findViewById(R.id.EditTextConfirmPassword);

        toastMessage = new CustomToastMessage(this,"Failed to create account. Please check your device network connection.",3);

        firebaseUserManager = new FirebaseUserManager();
        firebaseUserManager.initializeFirebase();


    }


    public void backToSignInButton(View view) {
        startActivity(new Intent(this, SignIn.class));
        finish();

    }

    @Override
    public void onBackPressed() {
        backToSignInButton(null);
    }


    public void CreateBttnClicked(View view) {
        String userEmail = email.getText().toString().trim();
        String userConfirmPassword = confirmPassword.getText().toString().trim();

    firebaseUserManager.createUser(name,email,phoneNumber,password,confirmPassword);
    if(firebaseUserManager.UserInputRequirementsFailedAtSignUp()){
        return;
    }

    firebaseUserManager.getFirebaseAuthenticate().createUserWithEmailAndPassword(userEmail,userConfirmPassword).addOnCompleteListener(task -> {
        if(task.isSuccessful()){
            firebaseUserManager.getCurrentUser();
            toastMessage.hideMessage();
            startActivity(new Intent(this,MainScreen.class));
            return;
        }
       toastMessage.showMessage();
    });

    }


}