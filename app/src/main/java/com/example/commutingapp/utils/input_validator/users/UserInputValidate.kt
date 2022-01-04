package com.example.commutingapp.utils.input_validator.users

class UserInputValidate constructor(var validate: ValidateInput) {



    fun isValid(): Boolean {
        return validate.isValid() == true
    }



}