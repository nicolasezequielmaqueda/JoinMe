package com.joinme.ui.screens.account

/** Up to two letters for the avatar: "ana.garcia@x.com" gives "AG", "tester@x.com" gives "T". */
fun initialsOf(email: String): String =
    email.substringBefore('@')
        .split('.', '_', '-', '+')
        .mapNotNull { it.firstOrNull { char -> char.isLetter() } }
        .take(2)
        .joinToString("")
        .uppercase()
