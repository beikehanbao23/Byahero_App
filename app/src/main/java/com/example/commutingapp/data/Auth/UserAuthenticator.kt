package com.example.commutingapp.data.Auth

import com.google.firebase.auth.AuthCredential

interface UserAuthenticator<T> {

    //auth
    fun createUserWithEmailAndPassword(email:String,password:String):T
    fun signOut()
    fun signInWithCredential(authCredential: AuthCredential):T
    fun signInWithEmailAndPassword(email: String,password: String):T

}