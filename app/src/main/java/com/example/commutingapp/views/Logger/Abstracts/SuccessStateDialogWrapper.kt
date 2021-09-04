package com.example.commutingapp.views.Logger.Abstracts

import android.content.Context
import com.example.commutingapp.views.Logger.interfaces.DialogPresenter
import id.ionbit.ionalert.IonAlert

  class SuccessDialogWrapper(var context: Context): DialogPresenter {
      override fun showCustomDialog(title: String,contentText: String) {
          IonAlertInstance(context).dialogIonAlert(title,contentText,IonAlert.SUCCESS_TYPE).apply {
              if (isShowing) dismissWithAnimation() else show()
          }
      }




 }