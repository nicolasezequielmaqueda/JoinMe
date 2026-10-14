package com.joinme.model

/**
 * Predefined activity types, so creating an activity only takes a few taps.
 *
 * [defaultParticipants] is the suggested maximum when the user picks this type.
 * [emoji] is only decorative; the name is always shown next to it.
 */
enum class ActivityType(
    val displayName: String,
    val emoji: String,
    val category: ActivityCategory,
    val defaultParticipants: Int,
) {
    FOOTBALL("Football", "⚽", ActivityCategory.SPORTS, 10),
    PADEL("Padel", "🎾", ActivityCategory.SPORTS, 4),
    BADMINTON("Badminton", "🏸", ActivityCategory.SPORTS, 2),
    TABLE_TENNIS("Table Tennis", "🏓", ActivityCategory.GAMES, 2),
    POOL("Pool", "🎱", ActivityCategory.GAMES, 2),
    TABLE_FOOTBALL("Table Football", "⚽", ActivityCategory.GAMES, 4),
    DARTS("Darts", "🎯", ActivityCategory.GAMES, 4),
    BOARD_GAMES("Board Games", "🎲", ActivityCategory.GAMES, 4),
    CARDS("Cards", "🃏", ActivityCategory.GAMES, 4),
    MOVIE_NIGHT("Movie Night", "🎬", ActivityCategory.SOCIAL, 10),
    OTHER("Other", "✨", ActivityCategory.OTHER, 4),
}
