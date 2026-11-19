package com.joinme.ui.screens.activities

import com.joinme.ui.components.ActivityItem
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

/** Headers that split the Activities list by how soon each activity happens. */
enum class DayGroup(val label: String) {
    TODAY("Today"),
    TOMORROW("Tomorrow"),
    THIS_WEEK("This week"),
    LATER("Later"),
}

/** An activity that started yesterday and ends after midnight still counts as today. */
fun dayGroupOf(date: LocalDate, today: LocalDate): DayGroup = when {
    date <= today -> DayGroup.TODAY
    date == today.plus(1, DateTimeUnit.DAY) -> DayGroup.TOMORROW
    date < today.plus(7, DateTimeUnit.DAY) -> DayGroup.THIS_WEEK
    else -> DayGroup.LATER
}

/** Items of one group. The input is sorted by date, so the groups come in order too. */
data class DaySection(val group: DayGroup, val items: List<ActivityItem>)

fun groupByDay(items: List<ActivityItem>, today: LocalDate): List<DaySection> =
    items
        .groupBy { dayGroupOf(it.activity.date, today) }
        .map { (group, groupItems) -> DaySection(group, groupItems) }
