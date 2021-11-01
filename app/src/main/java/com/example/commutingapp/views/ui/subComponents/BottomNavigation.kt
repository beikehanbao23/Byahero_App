package com.example.commutingapp.views.ui.subComponents

import android.content.Context
import com.example.commutingapp.utils.others.FragmentToActivity

class BottomNavigation(private val notifyListener:FragmentToActivity):IComponent {


    override fun show() {
        notifyListener.onSecondNotify()
    }

    override fun hide() {
        notifyListener.onFirstNotify()
    }
}