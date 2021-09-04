package com.example.commutingapp.views.Logger

import com.example.commutingapp.views.Logger.interfaces.ResultStateDialogPresenter

class CustomDialogManager(private var resultStateDialogPresenter: ResultStateDialogPresenter) {

    fun showDialog(title:String,contentText:String){
        resultStateDialogPresenter.showDialog(title,contentText)
    }

}