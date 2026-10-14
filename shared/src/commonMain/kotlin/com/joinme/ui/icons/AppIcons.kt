package com.joinme.ui.icons

import androidx.compose.ui.graphics.vector.ImageVector
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.bold.CaretLeft
import com.adamglin.phosphoricons.bold.CaretRight
import com.adamglin.phosphoricons.bold.Minus
import com.adamglin.phosphoricons.bold.Plus
import com.adamglin.phosphoricons.fill.Cards
import com.adamglin.phosphoricons.fill.Compass
import com.adamglin.phosphoricons.fill.DiceFive
import com.adamglin.phosphoricons.fill.PingPong
import com.adamglin.phosphoricons.fill.Popcorn
import com.adamglin.phosphoricons.fill.Racquet
import com.adamglin.phosphoricons.fill.SoccerBall
import com.adamglin.phosphoricons.fill.Sparkle
import com.adamglin.phosphoricons.fill.Target
import com.adamglin.phosphoricons.fill.TennisBall
import com.adamglin.phosphoricons.fill.NumberCircleEight
import com.adamglin.phosphoricons.fill.GameController
import com.adamglin.phosphoricons.fill.CalendarCheck
import com.adamglin.phosphoricons.regular.CalendarBlank
import com.adamglin.phosphoricons.regular.Clock
import com.adamglin.phosphoricons.regular.Couch
import com.adamglin.phosphoricons.regular.Door
import com.adamglin.phosphoricons.regular.Hourglass
import com.adamglin.phosphoricons.regular.MapPin
import com.adamglin.phosphoricons.regular.Notepad
import com.adamglin.phosphoricons.regular.SquaresFour
import com.adamglin.phosphoricons.regular.Television
import com.adamglin.phosphoricons.regular.Tree
import com.adamglin.phosphoricons.regular.Users
import com.adamglin.phosphoricons.regular.GameController as GameControllerRegular
import com.adamglin.phosphoricons.regular.CourtBasketball as CourtBasketballRegular
import com.adamglin.phosphoricons.regular.Compass as CompassRegular
import com.adamglin.phosphoricons.regular.CalendarCheck as CalendarCheckRegular
import com.joinme.model.ActivityType
import com.joinme.model.ResidenceLocation

/**
 * All the icons of the app in one place (Phosphor Icons, MIT licence).
 * "Fill" icons are used for activity types and selected tabs,
 * "Regular" icons for information rows, "Bold" for small controls.
 */
object AppIcons {
    // Tab bar
    val DiscoverSelected: ImageVector get() = PhosphorIcons.Fill.Compass
    val Discover: ImageVector get() = PhosphorIcons.Regular.CompassRegular
    val MyActivitiesSelected: ImageVector get() = PhosphorIcons.Fill.CalendarCheck
    val MyActivities: ImageVector get() = PhosphorIcons.Regular.CalendarCheckRegular

    // Controls
    val Back: ImageVector get() = PhosphorIcons.Bold.CaretLeft
    val Chevron: ImageVector get() = PhosphorIcons.Bold.CaretRight
    val Plus: ImageVector get() = PhosphorIcons.Bold.Plus
    val Minus: ImageVector get() = PhosphorIcons.Bold.Minus

    // Information rows
    val Date: ImageVector get() = PhosphorIcons.Regular.CalendarBlank
    val Time: ImageVector get() = PhosphorIcons.Regular.Clock
    val Duration: ImageVector get() = PhosphorIcons.Regular.Hourglass
    val Location: ImageVector get() = PhosphorIcons.Regular.MapPin
    val People: ImageVector get() = PhosphorIcons.Regular.Users
    val Category: ImageVector get() = PhosphorIcons.Regular.SquaresFour
    val Notes: ImageVector get() = PhosphorIcons.Regular.Notepad
}

/** Icon that represents each activity type. */
val ActivityType.icon: ImageVector
    get() = when (this) {
        ActivityType.FOOTBALL -> PhosphorIcons.Fill.SoccerBall
        ActivityType.PADEL -> PhosphorIcons.Fill.TennisBall
        ActivityType.BADMINTON -> PhosphorIcons.Fill.Racquet
        ActivityType.TABLE_TENNIS -> PhosphorIcons.Fill.PingPong
        ActivityType.POOL -> PhosphorIcons.Fill.NumberCircleEight
        ActivityType.TABLE_FOOTBALL -> PhosphorIcons.Fill.GameController
        ActivityType.DARTS -> PhosphorIcons.Fill.Target
        ActivityType.BOARD_GAMES -> PhosphorIcons.Fill.DiceFive
        ActivityType.CARDS -> PhosphorIcons.Fill.Cards
        ActivityType.MOVIE_NIGHT -> PhosphorIcons.Fill.Popcorn
        ActivityType.OTHER -> PhosphorIcons.Fill.Sparkle
    }

/** Icon that represents each place of the residence. */
val ResidenceLocation.icon: ImageVector
    get() = when (this) {
        ResidenceLocation.COMMON_ROOM -> PhosphorIcons.Regular.Couch
        ResidenceLocation.GAMES_ROOM -> PhosphorIcons.Regular.GameControllerRegular
        ResidenceLocation.SPORTS_COURT -> PhosphorIcons.Regular.CourtBasketballRegular
        ResidenceLocation.CINEMA_ROOM -> PhosphorIcons.Regular.Television
        ResidenceLocation.GARDEN -> PhosphorIcons.Regular.Tree
        ResidenceLocation.ENTRANCE -> PhosphorIcons.Regular.Door
        ResidenceLocation.OTHER -> PhosphorIcons.Regular.MapPin
    }
