package com.example.commutingapp.data.firebase.usr

class UserEmailProcessor<T> constructor(private val userEmail: UserEmail<T>) {


    fun getUserEmail():String?{
        return userEmail.getUserEmail()
    }
    fun reloadEmail():T{
        return userEmail.reloadEmail()
    }
    fun sendEmailVerification():T{
        return userEmail.sendEmailVerification()
    }
    fun isEmailVerified():Boolean?{
        return userEmail.isEmailVerified()
    }
}