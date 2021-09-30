package com.example.commutingapp.utils.input_validator.users

interface ValidateInput {
    fun validationEmailFailed():Boolean?
    fun validationConfirmPasswordFailed():Boolean?
    fun validationPasswordFailed():Boolean?
}