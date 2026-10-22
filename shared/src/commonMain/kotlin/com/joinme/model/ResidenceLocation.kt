package com.joinme.model

/**
 * Shared places inside the residence.
 *
 * JoinMe does not use GPS: the user only chooses one of these common places,
 * so this field does not store a private address or GPS position.
 */
enum class ResidenceLocation(val displayName: String) {
    COMMON_ROOM("Common Room"),
    GAMES_ROOM("Games Room"),
    SPORTS_COURT("Sports Court"),
    CINEMA_ROOM("Cinema Room"),
    GARDEN("Garden"),
    ENTRANCE("Entrance"),
    OTHER("Other"),
}
