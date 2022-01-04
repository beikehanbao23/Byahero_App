package com.example.commutingapp.utils.input_validator.users

import android.content.Context
import android.util.Patterns
import android.widget.EditText
import com.example.commutingapp.R.string
import com.example.commutingapp.utils.input_validator.Validate
import com.example.commutingapp.utils.others.Constants.USER_INPUT_MINIMUM_NUMBER_OF_CHARACTERS


open class ValidateInputModel constructor(
    private var context: Context,
    private var email: EditText,
    private var password: EditText,
    private var confirmPassword: EditText?
) : ValidateInput {
    private val userConfirmPassword = confirmPassword?.text.toString().trim()
    private val userPassword = password.text.toString().trim()
    private val userEmail: String = email.text.toString().trim()



    private fun validationEmailFailed(): Boolean? = setEmailErrorText()?.isNotEmpty()
    private fun setEmailErrorText(): CharSequence?{
        email.error = when {
            userEmail.isEmpty() -> context.getString(string.fieldLeftBlankMessage)
            !validEmail() -> context.getString(string.emailIsInvalidMessage)
            else -> null
        }.also { email.requestFocus() }

        return email.error
    }
    private fun validEmail() = Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()



    private fun validationConfirmPasswordFailed():Boolean? = setConfirmPasswordErrorText()?.isNotEmpty()
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
                (Validate.containsNumeric(userConfirmPassword) ||
                        Validate.containSpecialCharacters(userConfirmPassword))
    }
    private fun isNumberOfCharactersLongEnough() = userConfirmPassword.toCharArray().size >= USER_INPUT_MINIMUM_NUMBER_OF_CHARACTERS



    private fun validationPasswordFailed() = setPasswordError()?.isNotEmpty()
    private fun setPasswordError():CharSequence?{
        password.error = when{
            userPassword.isEmpty()->context.getString(string.fieldLeftBlankMessage)
            else-> null
        }.also { password.requestFocus() }
        return password.error
    }

    override fun isValid(): Boolean? {
        return !(validationEmailFailed() == true || validationPasswordFailed() == true || validationConfirmPasswordFailed() == true)
    }


}