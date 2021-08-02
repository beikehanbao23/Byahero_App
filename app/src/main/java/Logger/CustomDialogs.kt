package Logger

import android.content.Context
import id.ionbit.ionalert.IonAlert

class CustomDialogs(val context:Context) {
    private fun dialogOf(title: String, contextText: String, type: Int): IonAlert {
        val alertDialog = IonAlert(context, type)
        alertDialog.titleText = title
        alertDialog.contentText = contextText
        return alertDialog
    }

    fun showErrorDialog(title: String, contextText: String) {
        dialogOf(title, contextText, IonAlert.ERROR_TYPE).show()
    }

    fun showSuccessDialog(title: String, contentText: String) {
        dialogOf(title, contentText, IonAlert.SUCCESS_TYPE).show()
    }

    fun showWarningDialog(title: String, contentText: String) {
        dialogOf(title, contentText, IonAlert.WARNING_TYPE).show()
    }
}