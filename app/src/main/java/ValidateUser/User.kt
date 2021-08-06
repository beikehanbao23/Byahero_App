package ValidateUser

import android.content.Context
import android.util.Patterns
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.commutingapp.R.string

open class User {
    private var email: EditText? = null
    private var password: EditText? = null
    private var confirmPassword: EditText? = null
    private var context: Context? = null
    private val appCompatActivity: AppCompatActivity? = null

    constructor(email: EditText?, password: EditText?, confirmPassword: EditText?) {
        this.email = email
        this.password = password
        this.confirmPassword = confirmPassword
    }

    constructor(email: EditText?, password: EditText?) {
        this.email = email
        this.password = password
    }

    constructor() {}

    fun setContext(context: Context?) {
        if (context == null) throw RuntimeException("Context cannot be null")
        this.context = context
    }

    fun validateEmailFailed(): Boolean {
        val emailInput = email!!.text.toString().trim { it <= ' ' }
        if (isNull(emailInput)) {
            email!!.error = context!!.getString(string.fieldLeftBlankMessage)
            email!!.requestFocus()
            return true
        }
        if (!validEmail()) {
            email!!.error = context!!.getString(string.emailIsInvalidMessage)
            email!!.requestFocus()
            return true
        }
        email!!.error = null
        return false
    }

    fun validateConfirmPasswordFailed(): Boolean {
        val confirmPasswordInput = confirmPassword!!.text.toString().trim { it <= ' ' }
        if (isNull(confirmPasswordInput)) {
            confirmPassword!!.error = context!!.getString(string.fieldLeftBlankMessage)
            confirmPassword!!.requestFocus()
            return true
        }
        if (passwordIsNotMatch()) {
            confirmPassword!!.error = context!!.getString(string.passwordIsNotMatchMessage)
            confirmPassword!!.requestFocus()
            return true
        }
        if (!isPasswordStrong) {
            confirmPassword!!.error = context!!.getString(string.passwordIsWeakMessage)
            confirmPassword!!.requestFocus()
            return true
        }
        confirmPassword!!.error = null
        return false
    }

    fun validatePasswordFailed(): Boolean {
        val passwordInput = password!!.text.toString().trim { it <= ' ' }
        if (isNull(passwordInput)) {
            password!!.error = context!!.getString(string.fieldLeftBlankMessage)
            password!!.requestFocus()
            return true
        }
        password!!.error = null
        return false
    }

    private fun passwordIsNotMatch(): Boolean {
        val userPassword = password!!.text.toString().trim { it <= ' ' }
        val userConfirmPassword = confirmPassword!!.text.toString().trim { it <= ' ' }
        return userPassword != userConfirmPassword
    }

    private val isPasswordStrong: Boolean
        private get() {
            val userConfirmPassword = confirmPassword!!.text.toString().trim { it <= ' ' }
            return userConfirmPassword.toCharArray().size >= 8 &&
                    (InputValidationRegex.hasNumeric(userConfirmPassword) ||
                            InputValidationRegex.hasSpecialCharacters(userConfirmPassword))
        }

    private fun validEmail(): Boolean {
        val userEmail = email!!.text.toString().trim { it <= ' ' }
        return Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()
    }

    private fun isNull(input: String): Boolean {
        return input.trim { it <= ' ' }.isEmpty()
    }
}