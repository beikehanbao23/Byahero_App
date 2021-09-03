package com.example.commutingapp.views.Logger

import com.example.commutingapp.views.Logger.interfaces.DialogPresenter

class CustomDialogProcessor(private var dialogPresenter: DialogPresenter) {

    fun showDialog(title:String,contentText:String){
        dialogPresenter.showCustomDialog(title,contentText)
    }

}