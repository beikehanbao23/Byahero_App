package com.example.commutingapp.views.Logger

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.View
import com.example.commutingapp.R
import com.example.commutingapp.utils.InternetConnection.ConnectionManager

class NoInternetDialog(context: Context) {

    private var dialog: Dialog = Dialog(context).apply {
        setContentView(R.layout.custom_dialog_no_internet)
        window?.attributes?.windowAnimations  = android.R.style.Animation_Dialog
        findViewById<View>(R.id.retry_button).setOnClickListener {
            if (ConnectionManager(context).internetConnectionAvailable()){
                dismiss()
            }
        }
        findViewById<View>(R.id.go_to_settings_Button).setOnClickListener{
            context.startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }


    fun showNoInternetDialog(){
        dialog.apply {
            if(isShowing) dismiss() else show()
        }
    }



    }









