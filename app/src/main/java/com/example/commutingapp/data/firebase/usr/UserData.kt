package com.example.commutingapp.data.firebase.usr

interface UserData<T> {
    fun saveCreatedAccount()
    fun getUserProviderData():T?
    fun getDisplayName():String?
    fun hasAccountRemainingInCache():Boolean
}