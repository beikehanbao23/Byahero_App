package com.example.commutingapp.views.Logger.Abstracts

import android.content.Context
import com.example.commutingapp.views.Logger.LowLevelClass.CustomNoInternetDialog
import com.example.commutingapp.views.Logger.interfaces.DialogPresenter
import id.ionbit.ionalert.IonAlert

  class IonAlertDialogWrapper(var context: Context): DialogPresenter {
     private val customNoInternetDialog = CustomNoInternetDialog(context)


    private fun dialogIonAlert(title: String, contextText: String, type: Int): IonAlert {
        return IonAlert(context, type).apply {
            titleText = title
            contentText = contextText
        }
    }

      override fun showCustomDialog() {
          TODO("Not yet implemented")
      }




 }