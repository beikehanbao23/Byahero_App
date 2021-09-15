package com.example.commutingapp.data.Usr

class UserDataProcessor<T> constructor(private val userData: UserData<T>) {
    fun saveCreatedAccount(){
        userData.saveCreatedAccount()
    }
    fun getUserProviderData():T?{
        return userData.getUserProviderData()
    }
    fun getDisplayName():String?{
        return userData.getDisplayName()
    }
    fun hasAccountRemainingInCache():Boolean{
        return userData.hasAccountRemainingInCache()
    }

}