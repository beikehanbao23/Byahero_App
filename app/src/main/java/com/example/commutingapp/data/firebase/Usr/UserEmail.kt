package com.example.commutingapp.data.firebase.Usr

interface UserEmail<T> {


    fun getUserEmail():String?
    fun reloadEmail(): T
    fun sendEmailVerification():T
    fun isEmailVerified():Boolean?
}