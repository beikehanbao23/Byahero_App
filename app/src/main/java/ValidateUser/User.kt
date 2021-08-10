package ValidateUser

import android.content.Context
import android.util.Patterns
import android.widget.EditText
import com.example.commutingapp.R.string

open class User(
    var context: Context,
    var email: EditText,
    var password: EditText,
    var confirmPassword: EditText?
) {

    fun validateEmailFailed(): Boolean {

        val emailInput: String = email.text.toString().trim()

        email.error = when {
            emailInput.isEmpty() -> context.getString(string.fieldLeftBlankMessage)
            !validEmail() -> context.getString(string.emailIsInvalidMessage)
            else -> null
        }.also { email.requestFocus() }

        return !email.error.isNullOrBlank()
    }

    fun validateConfirmPasswordFailed(): Boolean {
        val confirmPasswordInput = confirmPassword?.text.toString().trim()

        confirmPassword?.error = when {

            confirmPasswordInput.isEmpty() -> context.getString(string.fieldLeftBlankMessage)
            passwordIsNotMatch() -> context.getString(string.passwordIsNotMatchMessage)
            !isPasswordStrong() -> context.getString(string.passwordIsWeakMessage)
            else -> null

        }.also { confirmPassword?.requestFocus() }

        return !confirmPassword?.error.isNullOrBlank()
    }


    fun validatePasswordFailed(): Boolean {
        val passwordInput = password.text.toString().trim()

        password.error = when{
            passwordInput.isNullOrBlank()->context.getString(string.fieldLeftBlankMessage)
            else-> null
        }.also { password?.requestFocus() }

        return !password.error.isNullOrBlank()
    }

    private fun passwordIsNotMatch(): Boolean {
        val userPassword = password.text.toString().trim()
        val userConfirmPassword = confirmPassword?.text.toString().trim()
        return userPassword != userConfirmPassword
    }

    private fun isPasswordStrong(): Boolean {
        val userConfirmPassword = confirmPassword?.text.toString().trim()
        return userConfirmPassword.toCharArray().size >= 8 &&
                (InputValidationRegex.hasNumeric(userConfirmPassword) ||
                        InputValidationRegex.hasSpecialCharacters(userConfirmPassword))
    }

    private fun validEmail(): Boolean {
        val userEmail = email.text.toString().trim()
        return Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()
    }


}