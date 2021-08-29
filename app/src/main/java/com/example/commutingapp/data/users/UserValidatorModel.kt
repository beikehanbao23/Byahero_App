package com.example.commutingapp.data.users

import android.content.Context
import android.util.Patterns
import android.widget.EditText
import com.example.commutingapp.R.string
import com.example.commutingapp.utils.InputValidator.NumericNumberValidator
import com.example.commutingapp.utils.InputValidator.SpecialCharactersValidator


const val USER_INPUT_MINIMUM_NUMBER_OF_CHARACTERS = 8
open class UserValidatorModel constructor(
    private var context: Context,
    private var email: EditText,
    private var password: EditText,
    private var confirmPassword: EditText?
) : UserValidator {
    private val userConfirmPassword = confirmPassword?.text.toString().trim()
    private val userPassword = password.text.toString().trim()
    private val userEmail: String = email.text.toString().trim()



    override fun validationEmailFailed(): Boolean? = setEmailErrorText()?.isNotEmpty()
    private fun setEmailErrorText(): CharSequence?{
        email.error = when {
            userEmail.isEmpty() -> context.getString(string.fieldLeftBlankMessage)
            !validEmail() -> context.getString(string.emailIsInvalidMessage)
            else -> null
        }.also { email.requestFocus() }

        return email.error
    }
    private fun validEmail() = Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()



    override fun validationConfirmPasswordFailed():Boolean? = setConfirmPasswordErrorText()?.isNotEmpty()
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
    private fun isNumberOfCharactersLongEnough() = userConfirmPassword.toCharArray().size >= USER_INPUT_MINIMUM_NUMBER_OF_CHARACTERS



    override fun validationPasswordFailed() = setPasswordError()?.isNotEmpty()
    private fun setPasswordError():CharSequence?{
        password.error = when{
            userPassword.isNullOrBlank()->context.getString(string.fieldLeftBlankMessage)
            else-> null
        }.also { password.requestFocus() }
        return password.error
    }





}