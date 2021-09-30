package com.example.commutingapp.utils.input_validator
import com.example.commutingapp.data.others.Constants.REGEX_SPECIAL_CHARACTERS_VALUE

import com.example.commutingapp.data.others.Constants.REGEX_NUMBER_VALUE
import java.util.regex.Pattern



object Validate {

    fun containsNumeric(input: String): Boolean {
        return Pattern.compile(REGEX_NUMBER_VALUE).matcher(input).find()
    }
    fun containSpecialCharacters(input: String): Boolean {
        return Pattern.compile(REGEX_SPECIAL_CHARACTERS_VALUE).matcher(input).find()
    }

}