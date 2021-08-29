package com.example.commutingapp.data.users

interface UserValidator {
    fun validationEmailFailed():Boolean?
    fun validationConfirmPasswordFailed():Boolean?
    fun validationPasswordFailed():Boolean?
}