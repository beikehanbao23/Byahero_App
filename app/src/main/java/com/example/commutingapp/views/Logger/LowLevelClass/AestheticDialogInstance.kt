package com.example.commutingapp.views.Logger.LowLevelClass

import android.content.Context
import id.ionbit.ionalert.IonAlert

internal class IonAlertInstance(var context: Context) {
     fun dialogIonAlert(title: String, contextText: String, type: Int): IonAlert {
        return IonAlert(context, type).apply {
            titleText = title
            contentText = contextText
        }
    }


}