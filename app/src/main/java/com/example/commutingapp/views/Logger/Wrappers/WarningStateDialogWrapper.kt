package com.example.commutingapp.views.Logger.Wrappers

import android.app.Activity
import com.example.commutingapp.views.Logger.Interface.MutableDialogPresenter
import com.example.commutingapp.views.Logger.LowLevelImplementation.AestheticDialogInstance
import com.thecode.aestheticdialogs.DialogType

class WarningStateDialogWrapper(
    override var activity: Activity
    ):
    AestheticDialogInstance(activity),
    MutableDialogPresenter {

    override fun showDialog(title: String, contentText: String) {
        super.aestheticDialog(title,contentText, DialogType.WARNING).show()
    }

}