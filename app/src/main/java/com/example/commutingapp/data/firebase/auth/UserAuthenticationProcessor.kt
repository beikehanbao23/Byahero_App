package com.example.commutingapp.data.firebase.auth

class UserAuthenticationProcessor<T>

constructor(private val userAuthenticator: UserAuthenticator<T>):UserAuthenticator<T> by userAuthenticator


