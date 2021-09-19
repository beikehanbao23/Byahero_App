package com.example.commutingapp.views.Dialogs

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialog
import com.example.commutingapp.R

data class CustomDialogBuilder constructor(

    @NonNull val activity: Activity,
    @LayoutRes val layout: Int = R.layout.custom_dialog_no_internet,
    @StyleRes val animation: Int = android.R.style.Animation_Dialog,
    @NonNull val backgroundColorDrawable: ColorDrawable = ColorDrawable(Color.TRANSPARENT),
    val title: String = "Default Title",
    val description: String = "Default Description",


) : AppCompatDialog(activity) {

    init {
        setContentView(layout)
        window?.attributes?.windowAnimations = animation
        window?.setBackgroundDrawable(backgroundColorDrawable)

    }

}