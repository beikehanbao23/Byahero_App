package Validator

import java.util.regex.Pattern

object NumericNumberValidator {
    fun containsNumeric(input: String): Boolean {
        return Pattern.compile("[0-9]").matcher(input).find()
    }



}