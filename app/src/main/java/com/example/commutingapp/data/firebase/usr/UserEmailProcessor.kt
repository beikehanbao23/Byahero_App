package com.example.commutingapp.data.firebase.usr

class UserEmailProcessor<T> constructor(private val userEmail: UserEmail<T>):UserEmail<T> by userEmail