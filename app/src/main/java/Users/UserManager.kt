package ValidateUser

import android.content.Context
import android.widget.EditText

class UserManager(
    context: Context,
    email: EditText,
    password: EditText,
    confirmPassword: EditText?
) : User(context, email, password, confirmPassword) {


    fun userInputRequirementsFailedAtSignUp(): Boolean {
        return super.validateEmailFailed() || super.validatePasswordFailed() || super.validateConfirmPasswordFailed()
    }

    fun userInputRequirementsFailedAtSignIn(): Boolean {
        return super.validateEmailFailed() || super.validatePasswordFailed()
    }
}