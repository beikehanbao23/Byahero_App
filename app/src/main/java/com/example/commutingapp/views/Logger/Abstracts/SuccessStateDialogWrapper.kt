package com.example.commutingapp.views.Logger.Abstracts

import android.content.Context
import com.example.commutingapp.views.Logger.interfaces.MutableDialogPresenter
import id.ionbit.ionalert.IonAlert

  class SuccessStateDialogWrapper(var context: Context): MutableDialogPresenter {
      override fun showDialog(title: String, contentText: String) {
          IonAlertInstance(context).dialogIonAlert(title,contentText,IonAlert.SUCCESS_TYPE).apply {
              if (isShowing) dismissWithAnimation() else show()
          }
      }




 }