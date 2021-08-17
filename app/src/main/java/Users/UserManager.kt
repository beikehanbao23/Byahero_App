package Users

import android.content.Context
import android.widget.EditText

class UserManager constructor(
    context: Context,
    email: EditText,
    password: EditText,
    confirmPassword: EditText?
) : User(context, email, password, confirmPassword) {


    /**
     * kind of weird putting '== true' there, but it's part of kotlin null safety btw.
     */
    fun signUpValidationFail(): Boolean {
        return super.validateEmailFail() == true ||
                super.validatePasswordFail() == true||
                super.validateConfirmPasswordFail() == true
    }

    fun signInValidationFail(): Boolean {
        return super.validateEmailFail() == true ||
                super.validatePasswordFail() == true
    }
}