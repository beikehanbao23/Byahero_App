package ValidateUser

import android.content.Context
import android.util.Patterns
import android.widget.EditText
import com.example.commutingapp.R.string
open class User( var context: Context,  var email: EditText,  var password: EditText,  var confirmPassword: EditText?)  {



    fun validateEmailFailed(): Boolean {
        val emailInput = email.text.toString().trim()

        if (isNull(emailInput)) {
            email.error = context.getString(string.fieldLeftBlankMessage)
            email.requestFocus()
            return true
        }
        if (!validEmail()) {
            email.error = context.getString(string.emailIsInvalidMessage)
            email.requestFocus()
            return true
        }
        email.error = null
        return false
    }

    fun validateConfirmPasswordFailed(): Boolean {
        val confirmPasswordInput = confirmPassword?.text.toString().trim()
        if (isNull(confirmPasswordInput)) {
            confirmPassword?.error  = context.getString(string.fieldLeftBlankMessage)
            confirmPassword?.requestFocus()
            return true
        }
        if (passwordIsNotMatch()) {
            confirmPassword?.error  = context.getString(string.passwordIsNotMatchMessage)

            confirmPassword?.requestFocus()
            return true
        }
        if (!isPasswordStrong()) {
            confirmPassword?.error   = context.getString(string.passwordIsWeakMessage)
            confirmPassword?.requestFocus()
            return true
        }
        confirmPassword?.error = null
        return false
    }

    fun validatePasswordFailed(): Boolean {
        val passwordInput = password.text.toString().trim()

        if (isNull(passwordInput)) {
            password.error = context.getString(string.fieldLeftBlankMessage)
            password.requestFocus()
            return true

        }
        password.error = null
        return false
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

    private fun isNull(input: String) = input.trim().isEmpty()
    
}