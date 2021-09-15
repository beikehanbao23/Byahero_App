package com.example.commutingapp.views.Logger

import com.example.commutingapp.views.Logger.Interface.ImmutableDialogPresenter

class FixDialogManager
    constructor(
    private val immutableDialogPresenter: ImmutableDialogPresenter){

    fun showDialog() {
        immutableDialogPresenter.showDialog()
    }


}