package com.example.commutingapp.views.Logger.Wrappers

import android.content.Context
import com.example.commutingapp.views.Logger.LowLevelImplementation.CustomNoInternetDialog
import com.example.commutingapp.views.Logger.Interface.ImmutableDialogPresenter

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