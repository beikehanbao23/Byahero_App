package com.example.commutingapp.data.Authenticate

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential

interface Authenticator {

    fun saveCreatedAccount():Unit
    fun getUserEmail():String
    fun reloadEmail():Task<Void>
    fun sendEmailVerification():Task<Void>
    fun getUserProviderData():Unit
    fun isEmailVerified():Boolean
    fun getDisplayName():String

    fun createUserWithEmailAndPassword(email:String,password:String):Unit
    fun signOut():Unit
    fun signInWithCredential(authCredential: AuthCredential):Unit
    fun signInWithEmailAndPassword(email: String,password: String):Unit

}