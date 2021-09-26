package com.example.commutingapp.utils.input_validator.users

interface UserValidator {
    fun validationEmailFailed():Boolean?
    fun validationConfirmPasswordFailed():Boolean?
    fun validationPasswordFailed():Boolean?
}