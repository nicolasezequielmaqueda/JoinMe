package com.joinme.model

/**
 * Status of an activity. It is always shown as text on screen
 * (accessibility requirement: information must not depend only on colour).
 */
enum class ActivityStatus(val label: String) {
    OPEN("OPEN"),
    FULL("FULL"),
    CANCELLED("CANCELLED"),
    COMPLETED("COMPLETED"),
}
