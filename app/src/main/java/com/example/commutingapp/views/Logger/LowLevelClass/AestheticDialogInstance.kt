package com.example.commutingapp.views.Logger.LowLevelClass

import android.app.Activity
import android.view.Gravity
import com.thecode.aestheticdialogs.*

open class AestheticDialogInstance(open var activity: Activity) {
     fun aestheticDialog(title: String, message: String, type: DialogType): AestheticDialog.Builder {

         return AestheticDialog.Builder(activity, DialogStyle.FLAT,type).apply {
           setTitle(title)
           setMessage(message)
           setCancelable(true)
           setDarkMode(false)

           setGravity(Gravity.CENTER)
           setAnimation(DialogAnimation.SHRINK)
           setOnClickListener(object :OnDialogClickListener{
               override fun onClick(dialog: AestheticDialog.Builder) {
                dismiss()
               }
           })

           }

       }


    }


