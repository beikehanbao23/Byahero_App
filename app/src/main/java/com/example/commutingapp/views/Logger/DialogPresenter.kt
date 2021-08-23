package com.example.commutingapp.views.Logger

interface DialogPresenter {
    fun showErrorDialog(title: String, contextText: String)
    fun showSuccessDialog(title: String, contextText: String)
    fun showWarningDialog(title: String, contextText: String)
}