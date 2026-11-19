package com.joinme.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AuthValidatorTest {

    private val validPassword = "12345678"

    @Test
    fun validEmailAndPasswordAreAccepted() {
        assertNull(AuthValidator.validate("ana@joinme.test", validPassword))
    }

    @Test
    fun emptyEmailIsNotValid() {
        assertEquals("Enter your email.", AuthValidator.validate("", validPassword))
        assertEquals("Enter your email.", AuthValidator.validate("   ", validPassword))
    }

    @Test
    fun emailWithoutAtSignIsNotValid() {
        assertEquals("Enter a valid email address.", AuthValidator.validate("ana.joinme.test", validPassword))
    }

    @Test
    fun emailWithoutDomainIsNotValid() {
        assertNotNull(AuthValidator.validate("ana@joinme", validPassword))
        assertNotNull(AuthValidator.validate("ana@", validPassword))
    }

    @Test
    fun emailWithSpacesIsNotValid() {
        assertNotNull(AuthValidator.validate("ana lopez@joinme.test", validPassword))
        assertNotNull(AuthValidator.validate(" ana@joinme.test", validPassword))
    }

    @Test
    fun emptyPasswordIsNotValid() {
        assertEquals("Enter your password.", AuthValidator.validate("ana@joinme.test", ""))
    }

    @Test
    fun passwordOfSevenCharactersIsNotValid() {
        assertNotNull(AuthValidator.validate("ana@joinme.test", "1234567"))
    }

    @Test
    fun passwordOfEightCharactersIsValid() {
        assertNull(AuthValidator.validate("ana@joinme.test", "12345678"))
    }
}
