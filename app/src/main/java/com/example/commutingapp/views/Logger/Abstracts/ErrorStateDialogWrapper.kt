package com.example.commutingapp.views.Logger.Abstracts

import android.app.Activity
import com.example.commutingapp.views.Logger.LowLevelClass.AestheticDialogInstance
import com.example.commutingapp.views.Logger.interfaces.MutableDialogPresenter
import com.thecode.aestheticdialogs.DialogType

class ErrorStateDialogWrapper(
    override var activity: Activity
    ) :
    AestheticDialogInstance(activity),
    MutableDialogPresenter {

    override fun showDialog(title: String, contentText: String) {
        super.aestheticDialog(title, contentText, DialogType.ERROR).show()
    }


}