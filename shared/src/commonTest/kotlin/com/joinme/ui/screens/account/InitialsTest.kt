package com.joinme.ui.screens.account

import kotlin.test.Test
import kotlin.test.assertEquals

class InitialsTest {

    @Test
    fun oneLetterForASingleWord() {
        assertEquals("T", initialsOf("tester@joinme.test"))
    }

    @Test
    fun twoLettersForANameWithSeparator() {
        assertEquals("AG", initialsOf("ana.garcia@joinme.test"))
        assertEquals("JP", initialsOf("juan_perez_lopez@joinme.test"))
    }

    @Test
    fun emptyEmailGivesNoInitials() {
        assertEquals("", initialsOf(""))
    }
}
