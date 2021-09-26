package com.example.commutingapp.utils.InputValidator.users

interface UserValidator {
    fun validationEmailFailed():Boolean?
    fun validationConfirmPasswordFailed():Boolean?
    fun validationPasswordFailed():Boolean?
}