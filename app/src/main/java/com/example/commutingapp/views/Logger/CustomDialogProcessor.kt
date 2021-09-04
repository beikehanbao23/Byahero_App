package com.example.commutingapp.views.Logger

import android.app.Dialog
import android.content.Context
import com.example.commutingapp.views.Logger.Abstracts.ErrorStateDialogWrapper
import com.example.commutingapp.views.Logger.Abstracts.NoInternetDialogWrapper
import com.example.commutingapp.views.Logger.Abstracts.SuccessStateDialogWrapper
import com.example.commutingapp.views.Logger.Abstracts.WarningStateDialogWrapper

class CustomDialogProcessor(var context:Context) {
    private val noInternetDialogWrapper = NoInternetDialogWrapper(context)
    private val noInternetDialog = FixDialogManager(noInternetDialogWrapper)

    fun showSuccessDialog(title:String,contentText:String) {
       StateDialogManager(SuccessStateDialogWrapper(context)).apply{
           showDialog(title,contentText)
       }
    }

    fun showErrorDialog(title:String,contentText: String){
        StateDialogManager(ErrorStateDialogWrapper(context)).apply{
            showDialog(title,contentText)
        }
    }

    fun showWarningDialog(title:String,contentText: String){
        StateDialogManager(WarningStateDialogWrapper(context)).apply{
            showDialog(title,contentText)
        }
    }

    fun showNoInternetDialog(){
        noInternetDialog.showDialog()
    }

    fun noInternetDialogCallback(): Dialog = noInternetDialogWrapper.dialog
}