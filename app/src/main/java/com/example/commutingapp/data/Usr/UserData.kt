package com.example.commutingapp.data.Usr

interface UserData<T> {
    fun saveCreatedAccount()
    fun getUserProviderData():T?
    fun getDisplayName():String?
    fun hasAccountRemainingInCache():Boolean
}