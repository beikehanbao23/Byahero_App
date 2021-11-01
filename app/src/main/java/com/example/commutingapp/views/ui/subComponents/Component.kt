package com.example.commutingapp.views.ui.subComponents

class Component(private val subcomponent: IComponent) {
    fun show(){
        subcomponent.show()
    }
    fun hide(){
        subcomponent.hide()
    }
}