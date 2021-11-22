package com.example.commutingapp.views.ui.subComponents

import androidx.fragment.app.Fragment
import com.example.commutingapp.utils.others.FragmentToActivity

class BottomNavigation(private val notifyListener:FragmentToActivity<Fragment>):IComponent {


    override fun show() {
        notifyListener.onSecondNotify()
    }

    override fun hide() {
        notifyListener.onFirstNotify()
    }
}