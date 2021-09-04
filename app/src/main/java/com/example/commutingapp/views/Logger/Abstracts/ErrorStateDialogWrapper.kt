package com.example.commutingapp.views.Logger.Abstracts

import android.content.Context
import com.example.commutingapp.views.Logger.interfaces.ResultStateDialogPresenter
import id.ionbit.ionalert.IonAlert

class ErrorResultStateDialogWrapper(var context: Context):ResultStateDialogPresenter {
    override fun showDialog(title: String, contentText: String) {
        IonAlertInstance(context).dialogIonAlert(title,contentText, IonAlert.ERROR_TYPE).apply {
            if (isShowing) dismissWithAnimation() else show()
        }
    }
}