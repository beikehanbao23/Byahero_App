package com.example.commutingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Signup extends AppCompatActivity {
private EditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        name = findViewById(R.id.editTextTextPersonName2);
    }

    public void backToSignInButton(View view) {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        backToSignInButton(null);
    }


    public void CreateButtonClicked(View view) {

    }
}