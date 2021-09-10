package com.example.commutingapp.views.Logger.Abstracts

import android.content.Context
import com.example.commutingapp.views.Logger.LowLevelClass.CustomNoInternetDialog
import com.example.commutingapp.views.Logger.interfaces.ImmutableDialogPresenter

class NoInternetDialogWrapper(
    context: Context
    ) :
    CustomNoInternetDialog(context),
    ImmutableDialogPresenter {


    override var dialog = super.dialog

    override fun showDialog() {
        super.showNoInternetDialog()
    }


}