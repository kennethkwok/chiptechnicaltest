package com.kennethkwok.chiptechnicaltest.util

/**
 * Capitalises the first character in the String
 */
fun String.capitaliseFirstLetter(): String {
    return if (isNotEmpty()) {
        this[0].uppercaseChar() + substring(1)
    } else {
        this
    }
}
