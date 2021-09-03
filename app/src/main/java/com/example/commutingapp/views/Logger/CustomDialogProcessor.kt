package com.example.commutingapp.views.Logger

class CustomDialogProcessor(var customDialogPresenter: CustomDialogPresenter) {

    fun showErrorDialog(title: String, contextText: String){
        customDialogPresenter.showErrorDialog(title,contextText)
    }
    fun showSuccessDialog(title: String, contextText: String){
        customDialogPresenter.showSuccessDialog(title,contextText)
    }
    fun showWarningDialog(title: String, contextText: String){
        customDialogPresenter.showWarningDialog(title,contextText)
    }
    fun showNoInternetDialog(){

        customDialogPresenter.showNoInternetDialog()
    }
}