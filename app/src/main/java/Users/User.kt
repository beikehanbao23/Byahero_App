package Users

import Validator.NumericNumberValidator
import Validator.SpecialCharactersValidator
import android.content.Context
import android.util.Patterns
import android.widget.EditText
import com.example.commutingapp.R.string


const val MINIMUM_NUMBER_OF_CHARACTERS = 8
open class User constructor(
    private var context: Context,
    private var email: EditText,
    private var password: EditText,
    private var confirmPassword: EditText?
) {

    private val userConfirmPassword = confirmPassword?.text.toString().trim()
    private val userPassword = password.text.toString().trim()
    private val userEmail: String = email.text.toString().trim()



    fun validateEmailFail() = setEmailErrorText()?.isNotEmpty()
    private fun setEmailErrorText(): CharSequence?{
        email.error = when {
            userEmail.isEmpty() -> context.getString(string.fieldLeftBlankMessage)
            !validEmail() -> context.getString(string.emailIsInvalidMessage)
            else -> null
        }.also { email.requestFocus() }

        return email.error
    }
    private fun validEmail() = Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()



    fun validateConfirmPasswordFail() = setConfirmPasswordErrorText()?.isNotEmpty()
    private fun setConfirmPasswordErrorText(): CharSequence? {
        confirmPassword?.error = when {

            userConfirmPassword.isEmpty() -> context.getString(string.fieldLeftBlankMessage)
            passwordIsNotMatch() -> context.getString(string.passwordIsNotMatchMessage)
            !strongPassword() -> context.getString(string.passwordIsWeakMessage)
            else -> null

        }.also { confirmPassword?.requestFocus() }
        return confirmPassword?.error
    }
    private fun passwordIsNotMatch() = userPassword != userConfirmPassword
    private fun strongPassword() : Boolean {
       return isNumberOfCharactersLongEnough() &&
                (NumericNumberValidator.containsNumeric(userConfirmPassword) ||
                        SpecialCharactersValidator.containSpecialCharacters(userConfirmPassword))
    }
    private fun isNumberOfCharactersLongEnough() = userConfirmPassword.toCharArray().size >= MINIMUM_NUMBER_OF_CHARACTERS



    fun validatePasswordFail() = setPasswordError()?.isNotEmpty()
    private fun setPasswordError():CharSequence?{
        password.error = when{
            userPassword.isNullOrBlank()->context.getString(string.fieldLeftBlankMessage)
            else-> null
        }.also { password.requestFocus() }
        return password.error
    }





}