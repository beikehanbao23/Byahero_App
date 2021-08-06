package ValidateUser

import java.util.regex.Pattern

object InputValidationRegex {
    fun hasNumeric(input: String): Boolean {
        return Pattern.compile("[0-9]").matcher(input).find()
    }

    fun hasSpecialCharacters(input: String): Boolean {
        return Pattern.compile("[!#$%&*()_+=|<>?{}\\[\\]~]").matcher(input).find()
    }

}