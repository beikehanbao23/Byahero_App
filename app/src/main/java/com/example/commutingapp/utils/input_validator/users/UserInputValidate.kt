package com.example.commutingapp.utils.input_validator.users

class UserInputValidate constructor(var validate: ValidateInput) {


    /**kind of weird putting '== true' there, but it's part of kotlin null safety btw.*/
    fun isValid(): Boolean {
        return validate.isValid() == true
    }



}