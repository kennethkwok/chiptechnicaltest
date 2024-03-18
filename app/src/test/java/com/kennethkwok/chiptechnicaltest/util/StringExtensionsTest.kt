package com.kennethkwok.chiptechnicaltest.util

import org.junit.Assert
import org.junit.Test

class StringExtensionsTest {
    @Test
    fun `when non empty string then first letter is capitalised`() {
        Assert.assertEquals("String", "string".capitaliseFirstLetter())
    }

    @Test
    fun `when empty string then empty string is returned`() {
        Assert.assertEquals("", "".capitaliseFirstLetter())
    }
}
