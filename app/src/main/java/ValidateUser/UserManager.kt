package ValidateUser

import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class UserManager : User {
    constructor(
        appCompatActivity: AppCompatActivity?,
        context: Context?,
        email: EditText?,
        password: EditText?,
        confirmPassword: EditText?
    ) : super(email, password, confirmPassword) {
        setContext(context)
    }

    constructor(
        appCompatActivity: AppCompatActivity?,
        context: Context?,
        email: EditText?,
        password: EditText?
    ) : super(email, password) {
        setContext(context)
    }

    fun UserInputRequirementsFailedAtSignUp(): Boolean {
        return validateEmailFailed() || validatePasswordFailed() || validateConfirmPasswordFailed()
    }

    fun UserInputRequirementsFailedAtSignIn(): Boolean {
        return validateEmailFailed() || validatePasswordFailed()
    }
}