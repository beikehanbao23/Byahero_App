package com.example.commutingapp.views.dialogs

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.provider.Settings
import android.view.Gravity
import android.view.View
import com.example.commutingapp.R
import com.example.commutingapp.utils.InternetConnection.ConnectionManager
import com.thecode.aestheticdialogs.*

class DialogDirector(val activity: Activity) {

    fun constructNoInternetDialog(): CustomDialogBuilder {
        return CustomDialogBuilder(
            activity = activity,
            layout = R.layout.custom_dialog_no_internet,
            animation = android.R.style.Animation_Dialog,
            backgroundColorDrawable = ColorDrawable(Color.TRANSPARENT)

        ).also { it.show() }.apply {
            findViewById<View>(R.id.retry_button)?.setOnClickListener {
                if (ConnectionManager(activity).internetConnectionAvailable()) {
                    dismiss()
                }
                findViewById<View>(R.id.go_to_settings_Button)?.setOnClickListener {
                    context.startActivity(Intent(Settings.ACTION_SETTINGS))
                }

            }
        }
    }


    fun constructWarningDialog(title: String, message: String) {

        AestheticDialog.Builder(activity, DialogStyle.FLAT, DialogType.WARNING)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(true)
            .setDarkMode(false)
            .setGravity(Gravity.CENTER)
            .setAnimation(DialogAnimation.SHRINK)
            .setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: AestheticDialog.Builder) {
                    dialog.dismiss()
                }
            }).show()


    }


    fun constructErrorDialog(title: String, message: String) {

        AestheticDialog.Builder(activity, DialogStyle.FLAT, DialogType.ERROR)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(true)
            .setDarkMode(false)
            .setGravity(Gravity.CENTER)
            .setAnimation(DialogAnimation.SHRINK)
            .setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: AestheticDialog.Builder) {
                    dialog.dismiss()
                }
            }).show()


    }

    fun constructSuccessDialog(title: String, message: String) {


        AestheticDialog.Builder(activity, DialogStyle.FLAT, DialogType.SUCCESS)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(true)
            .setDarkMode(false)
            .setGravity(Gravity.CENTER)
            .setAnimation(DialogAnimation.SHRINK)
            .setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: AestheticDialog.Builder) {
                    dialog.dismiss()
                }
            }).show()
    }

}



