package com.example.commutingapp.data.Auth

import com.google.firebase.auth.AuthCredential

class UserAuthenticationProcessor<T>

constructor(private val userAuthenticator: UserAuthenticator<T>) {


    fun createUserWithEmailAndPassword(email: String, password: String): T {
        return userAuthenticator.createUserWithEmailAndPassword(email, password)
    }

    fun signOut(): Unit {
        userAuthenticator.signOut()
    }

    fun signInWithCredential(authCredential: AuthCredential): T {
       return userAuthenticator.signInWithCredential(authCredential)
    }

    fun signInWithEmailAndPassword(email: String, password: String): T {
    return userAuthenticator.signInWithEmailAndPassword(email, password)
    }

}