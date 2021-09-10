package com.example.commutingapp.views.Logger

import com.example.commutingapp.views.Logger.interfaces.MutableDialogPresenter

class StateDialogManager
constructor(
    private var mutableDialogPresenter: MutableDialogPresenter) {

    fun showDialog(title:String,contentText:String){
        mutableDialogPresenter.showDialog(title,contentText)
    }

}