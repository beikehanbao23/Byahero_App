package com.example.commutingapp.data.firebase.usr

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo

class FirebaseUserWrapper : UserData<List< UserInfo>?>, UserEmail<Task<Void>?> {
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun saveCreatedAccount() {
        firebaseUser = FirebaseAuth.getInstance().currentUser
    }

    override fun getUserEmail(): String? {
        return firebaseUser?.email
    }

    override fun reloadEmail(): Task<Void>? {
        return firebaseUser?.reload()
    }

    override fun sendEmailVerification(): Task<Void>? {
        return firebaseUser?.sendEmailVerification()
    }

    override fun getUserProviderData(): List<UserInfo>? {

        return firebaseUser?.providerData

    }

    override fun isEmailVerified(): Boolean? {
        return firebaseUser?.isEmailVerified
    }

    override fun getDisplayName(): String? {
        return firebaseUser?.displayName
    }

    override fun hasAccountRemainingInCache(): Boolean {
     return firebaseUser != null
    }
}