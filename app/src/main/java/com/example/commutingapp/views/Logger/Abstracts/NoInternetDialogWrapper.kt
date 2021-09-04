package com.example.commutingapp.views.Logger.Abstracts

import android.content.Context
import com.example.commutingapp.views.Logger.LowLevelClass.CustomNoInternetDialog
import com.example.commutingapp.views.Logger.interfaces.ImmutableDialogPresenter

class NoInternetDialogWrapper(private val context: Context) : ImmutableDialogPresenter {

    private var noInternetDialog:CustomNoInternetDialog = CustomNoInternetDialog(context)

    var dialog= noInternetDialog.dialog
    private set


    override fun showDialog() {
        noInternetDialog.showNoInternetDialog()
    }


}