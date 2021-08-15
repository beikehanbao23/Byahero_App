package Users

import android.content.Context
import android.widget.EditText

class UserManager constructor(
    context: Context,
    email: EditText,
    password: EditText,
    confirmPassword: EditText?
) : User(context, email, password, confirmPassword) {




    fun signUpValidationFail(): Boolean {
        return super.validateEmailFail() ||
                super.validatePasswordFail() ||
                super.validateConfirmPasswordFail() == true
    }

    fun signInValidationFail(): Boolean {
        return super.validateEmailFail() ||
                super.validatePasswordFail()
    }
}