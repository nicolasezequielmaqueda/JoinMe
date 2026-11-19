package com.joinme.domain

/**
 * Checks the email and password typed on the login screen.
 * The server checks the password again, so this only avoids useless requests.
 */
object AuthValidator {

    const val MIN_PASSWORD_LENGTH = 8

    private val EMAIL_FORMAT = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

    /**
     * Returns the first problem found as a message for the user,
     * or null if both values are valid.
     */
    fun validate(email: String, password: String): String? {
        if (email.isBlank()) return "Enter your email."
        if (!EMAIL_FORMAT.matches(email)) return "Enter a valid email address."
        if (password.isEmpty()) return "Enter your password."
        if (password.length < MIN_PASSWORD_LENGTH) {
            return "The password must have at least $MIN_PASSWORD_LENGTH characters."
        }
        return null
    }
}
