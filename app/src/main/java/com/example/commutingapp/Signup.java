package com.example.commutingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import RegisterUser.Register;

public class Signup extends AppCompatActivity {
private EditText name,email,phoneNumber,password,confirmPassword;
private Register register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        name = findViewById(R.id.EditTextName);
        email = findViewById(R.id.EditTextEmail);
        phoneNumber = findViewById(R.id.editTextPhone);
        password = findViewById(R.id.editTextPassword);
        confirmPassword = findViewById(R.id.EditTextConfirmPassword);



    }

    public void backToSignInButton(View view) {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        backToSignInButton(null);
    }





    public void CreateBttnClicked(View view) {

    }
}