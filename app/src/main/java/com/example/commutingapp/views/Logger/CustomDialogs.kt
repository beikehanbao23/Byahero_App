package com.example.commutingapp.views.Logger

import android.content.Context
import id.ionbit.ionalert.IonAlert

class CustomDialogs(val context: Context):CustomDialogPresenter {


    private fun dialogAlert(title: String, contextText: String, type: Int): IonAlert {
        val alertDialog = IonAlert(context, type)
        return alertDialog.apply {
            titleText = title
            contentText = contextText
        }
    }
    private fun dialogAlert():NoInternetDialog{
        NoInternetDialog(context).also {
           return it
       }
   }

    override fun showErrorDialog(title: String, contextText: String) {
        dialogAlert(title, contextText, IonAlert.ERROR_TYPE).apply {
            if (isShowing) dismiss() else show()
        }

    }

    override fun showSuccessDialog(title: String, contentText: String) {
        dialogAlert(title, contentText, IonAlert.SUCCESS_TYPE).apply {
            if (isShowing) dismiss() else show()
        }
    }

    override fun showWarningDialog(title: String, contentText: String) {
        dialogAlert(title, contentText, IonAlert.WARNING_TYPE).apply {
            if (isShowing) dismiss() else show()
        }
    }

    override fun showNoInternetDialog() {
        dialogAlert().showNoInternetDialog()
    }
}