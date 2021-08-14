package Validator

import java.util.regex.Pattern

object SpecialCharactersValidator {
    fun containSpecialCharacters(input: String): Boolean {
        return Pattern.compile("[!#$%&*()_+=|<>?{}\\[\\]~]").matcher(input).find()
    }
}