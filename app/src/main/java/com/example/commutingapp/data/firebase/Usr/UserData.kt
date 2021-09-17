package com.example.commutingapp.data.firebase.Usr

interface UserData<T> {
    fun saveCreatedAccount()
    fun getUserProviderData():T?
    fun getDisplayName():String?
    fun hasAccountRemainingInCache():Boolean
}