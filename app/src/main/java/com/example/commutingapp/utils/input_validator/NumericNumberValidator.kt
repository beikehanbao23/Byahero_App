package com.example.commutingapp.utils.InputValidator

import java.util.regex.Pattern

private const val REGEX_NUMBERVALUE="[0-9]"

object NumericNumberValidator {

    fun containsNumeric(input: String): Boolean {
        return Pattern.compile(REGEX_NUMBERVALUE).matcher(input).find()
    }
}