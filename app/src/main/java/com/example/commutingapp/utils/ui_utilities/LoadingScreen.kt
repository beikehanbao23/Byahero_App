package com.example.commutingapp.utils.ui_utilities

interface LoadingScreen {
    fun showLoading()
    fun finishLoading()
    fun processLoading(attributesVisibility:Boolean, progressBarVisibility:Int)

}