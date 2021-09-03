package com.example.commutingapp.views.Logger

import android.content.Context
import com.example.commutingapp.views.Logger.Abstracts.SuccessDialogWrapper

class CustomDialogProcessor(var context:Context) {


    fun showSuccessDialog(title:String,contentText:String) {
       CustomDialogManager(SuccessDialogWrapper(context)).apply{
           showDialog(title,contentText)
       }
    }

}