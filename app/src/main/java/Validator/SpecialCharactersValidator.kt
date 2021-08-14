package Validator

import java.util.regex.Pattern
private const val REGEX_SPECIAL_CHARACTERS_VALUE = "[!#$%&*()_+=|<>?{}\\[\\]~]"
object SpecialCharactersValidator {
    fun containSpecialCharacters(input: String): Boolean {
        return Pattern.compile(REGEX_SPECIAL_CHARACTERS_VALUE).matcher(input).find()
    }
}