package com.example.commutingapp.models.users

interface UserValidator {
    fun validationEmailFailed():Boolean?
    fun validationConfirmPasswordFailed():Boolean?
    fun validationPasswordFailed():Boolean?
}