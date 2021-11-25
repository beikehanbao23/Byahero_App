package com.example.commutingapp.data.firebase.usr

class UserDataProcessor<T> constructor(private val userData: UserData<T>): UserData<T> by userData