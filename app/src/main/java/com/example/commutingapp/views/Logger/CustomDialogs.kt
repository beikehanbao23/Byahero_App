package com.example.commutingapp.views.Logger

import android.content.Context
import id.ionbit.ionalert.IonAlert

class CustomDialogs(val context: Context):DialogPresenter {


    private fun dialogIonAlert(title: String, contextText: String, type: Int): IonAlert {
        val alertDialog = IonAlert(context, type)
        return alertDialog.apply {
            titleText = title
            contentText = contextText
        }
    }


    override fun showErrorDialog(title: String, contextText: String) {
        dialogIonAlert(title, contextText, IonAlert.ERROR_TYPE).apply {
            if (isShowing) cancel() else show()
        }

    }

    override fun showSuccessDialog(title: String, contentText: String) {
        dialogIonAlert(title, contentText, IonAlert.SUCCESS_TYPE).apply {
            if (isShowing) cancel() else show()
        }
    }

    override fun showWarningDialog(title: String, contentText: String) {
        dialogIonAlert(title, contentText, IonAlert.WARNING_TYPE).apply {
            if (isShowing) cancel() else show()
        }
    }
}