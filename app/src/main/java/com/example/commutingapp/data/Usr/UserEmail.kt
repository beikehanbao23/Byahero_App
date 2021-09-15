package com.example.commutingapp.data.Usr

interface UserEmail<T> {


    fun getUserEmail():String?
    fun reloadEmail(): T
    fun sendEmailVerification():T
    fun isEmailVerified():Boolean?
}