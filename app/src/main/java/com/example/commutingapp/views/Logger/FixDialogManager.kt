package com.example.commutingapp.views.Logger

import android.app.Dialog
import com.example.commutingapp.views.Logger.interfaces.DialogListeners
import com.example.commutingapp.views.Logger.interfaces.FixDialogPresenter

class FixDialogManager(
    private val fixDialogPresenter: FixDialogPresenter,
    private val dialogCallback: DialogListeners,
) {

    fun showDialog() {
        fixDialogPresenter.showDialog()
    }

    fun dialogCallback() : Dialog {
        return dialogCallback.dialogCallback()
    }

}