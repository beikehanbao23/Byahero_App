package com.example.commutingapp.views.Logger

import android.app.Activity
import android.app.Dialog
import com.example.commutingapp.views.Logger.Abstracts.ErrorStateDialogWrapper
import com.example.commutingapp.views.Logger.Abstracts.NoInternetDialogWrapper
import com.example.commutingapp.views.Logger.Abstracts.SuccessStateDialogWrapper
import com.example.commutingapp.views.Logger.Abstracts.WarningStateDialogWrapper

class CustomDialogProcessor( var activity: Activity) {
    private val noInternetDialogWrapper = NoInternetDialogWrapper(context = activity)
    private val noInternetDialog = FixDialogManager(noInternetDialogWrapper)

    fun showSuccessDialog(title:String,contentText:String) {
       StateDialogManager(SuccessStateDialogWrapper(activity)).apply{
           showDialog(title,contentText)

       }
    }

    fun showErrorDialog(title:String,contentText: String){
        StateDialogManager(ErrorStateDialogWrapper(activity)).apply{
            showDialog(title,contentText)
        }
    }

    fun showWarningDialog(title:String,contentText: String){
        StateDialogManager(WarningStateDialogWrapper(activity)).apply{
            showDialog(title,contentText)
        }
    }

    fun showNoInternetDialog(){
        noInternetDialog.showDialog()
    }

    fun noInternetDialogCallback(): Dialog = noInternetDialogWrapper.dialog
}