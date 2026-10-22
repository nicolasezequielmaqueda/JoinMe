package com.joinme.data

import com.joinme.model.Activity
import com.joinme.model.ActivityType
import com.joinme.model.ResidenceLocation
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/**
 * Made-up activities for the prototype, with fake user IDs.
 *
 * Dates are based on [today] when this list is created.
 * The initial list includes an activity today and a completed one yesterday.
 */
object SampleActivities {

    fun create(today: LocalDate): List<Activity> {
        val tomorrow = today.plus(1, DateTimeUnit.DAY)
        val inTwoDays = today.plus(2, DateTimeUnit.DAY)
        val inThreeDays = today.plus(3, DateTimeUnit.DAY)
        val yesterday = today.minus(1, DateTimeUnit.DAY)

        return listOf(
            Activity(
                id = "sample-1",
                type = ActivityType.TABLE_TENNIS,
                date = today,
                startTime = LocalTime(23, 0),
                durationMinutes = 60,
                location = ResidenceLocation.GAMES_ROOM,
                maxParticipants = 2,
                creatorId = "user-a",
                participantIds = listOf("user-a"),
                description = "Just for fun, any level is welcome.",
            ),
            Activity(
                id = "sample-2",
                type = ActivityType.FOOTBALL,
                date = inTwoDays,
                startTime = LocalTime(17, 0),
                durationMinutes = 90,
                location = ResidenceLocation.SPORTS_COURT,
                maxParticipants = 8,
                creatorId = "user-b",
                participantIds = listOf("user-b", "user-c", "user-d", "user-e"),
                description = "We have a ball, bring water.",
            ),
            Activity(
                id = "sample-3",
                type = ActivityType.MOVIE_NIGHT,
                date = tomorrow,
                startTime = LocalTime(21, 0),
                durationMinutes = 120,
                location = ResidenceLocation.CINEMA_ROOM,
                maxParticipants = 12,
                creatorId = "user-c",
                participantIds = listOf("user-c", "user-f", "user-g"),
                description = "Voting the movie when we meet.",
            ),
            Activity(
                id = "sample-4",
                type = ActivityType.POOL,
                date = tomorrow,
                startTime = LocalTime(19, 30),
                durationMinutes = 45,
                location = ResidenceLocation.GAMES_ROOM,
                maxParticipants = 2,
                creatorId = "user-d",
                participantIds = listOf("user-d", "user-h"), // already FULL
            ),
            Activity(
                id = "sample-5",
                type = ActivityType.BOARD_GAMES,
                date = inThreeDays,
                startTime = LocalTime(20, 0),
                durationMinutes = 120,
                location = ResidenceLocation.COMMON_ROOM,
                maxParticipants = 5,
                creatorId = CurrentUser.ID, // created by the user of the app
                participantIds = listOf(CurrentUser.ID, "user-e"),
                description = "Catan and Uno.",
            ),
            Activity(
                id = "sample-6",
                type = ActivityType.PADEL,
                date = inThreeDays,
                startTime = LocalTime(10, 0),
                durationMinutes = 60,
                location = ResidenceLocation.SPORTS_COURT,
                maxParticipants = 4,
                creatorId = "user-f",
                participantIds = listOf("user-f"),
                isCancelled = true,
            ),
            Activity(
                id = "sample-7",
                type = ActivityType.DARTS,
                date = yesterday,
                startTime = LocalTime(18, 0),
                durationMinutes = 60,
                location = ResidenceLocation.COMMON_ROOM,
                maxParticipants = 4,
                creatorId = "user-g",
                participantIds = listOf("user-g", CurrentUser.ID), // COMPLETED, joined by the user
            ),
        )
    }
}
