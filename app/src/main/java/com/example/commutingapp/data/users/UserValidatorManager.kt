package com.example.commutingapp.data.users

import android.content.Context
import android.widget.EditText

class UserValidatorManager constructor(
    context: Context,
    email: EditText,
    password: EditText,
    confirmPassword: EditText?
) : UserValidatorModel(context, email, password, confirmPassword) {


    /**kind of weird putting '== true' there, but it's part of kotlin null safety btw.*/
    fun signUpValidationFail(): Boolean {
        return super.validationEmailFailed() == true ||
                super.validationPasswordFailed() == true||
                super.validationConfirmPasswordFailed() == true
    }

    fun signInValidationFail(): Boolean {
        return super.validationEmailFailed() == true ||
                super.validationPasswordFailed() == true
    }

}