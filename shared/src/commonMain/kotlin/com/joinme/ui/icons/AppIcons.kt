package com.joinme.ui.icons

import androidx.compose.ui.graphics.vector.ImageVector
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Bold
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.bold.CaretLeft
import com.adamglin.phosphoricons.bold.Check
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
import com.adamglin.phosphoricons.regular.ArrowClockwise
import com.adamglin.phosphoricons.regular.CalendarBlank
import com.adamglin.phosphoricons.regular.CheckCircle
import com.adamglin.phosphoricons.regular.CloudRain
import com.adamglin.phosphoricons.regular.Clock
import com.adamglin.phosphoricons.regular.Couch
import com.adamglin.phosphoricons.regular.Door
import com.adamglin.phosphoricons.regular.Eye
import com.adamglin.phosphoricons.regular.EyeSlash
import com.adamglin.phosphoricons.regular.FileText
import com.adamglin.phosphoricons.regular.Hourglass
import com.adamglin.phosphoricons.regular.MapPin
import com.adamglin.phosphoricons.regular.ShareNetwork
import com.adamglin.phosphoricons.regular.SignOut
import com.adamglin.phosphoricons.regular.Television
import com.adamglin.phosphoricons.regular.Trash
import com.adamglin.phosphoricons.regular.Tree
import com.adamglin.phosphoricons.regular.Sun
import com.adamglin.phosphoricons.regular.UserCircle
import com.adamglin.phosphoricons.regular.Warning
import com.adamglin.phosphoricons.regular.WarningCircle
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
    val Check: ImageVector get() = PhosphorIcons.Bold.Check
    val CheckCircle: ImageVector get() = PhosphorIcons.Regular.CheckCircle
    val Plus: ImageVector get() = PhosphorIcons.Bold.Plus
    val Minus: ImageVector get() = PhosphorIcons.Bold.Minus
    val Account: ImageVector get() = PhosphorIcons.Regular.UserCircle
    val ShowPassword: ImageVector get() = PhosphorIcons.Regular.Eye
    val HidePassword: ImageVector get() = PhosphorIcons.Regular.EyeSlash
    val Warning: ImageVector get() = PhosphorIcons.Regular.Warning
    val WarningCircle: ImageVector get() = PhosphorIcons.Regular.WarningCircle
    val Refresh: ImageVector get() = PhosphorIcons.Regular.ArrowClockwise
    val Share: ImageVector get() = PhosphorIcons.Regular.ShareNetwork
    val InviteText: ImageVector get() = PhosphorIcons.Regular.FileText
    val SignOut: ImageVector get() = PhosphorIcons.Regular.SignOut
    val Delete: ImageVector get() = PhosphorIcons.Regular.Trash
    val Sunny: ImageVector get() = PhosphorIcons.Regular.Sun
    val Rainy: ImageVector get() = PhosphorIcons.Regular.CloudRain

    // Information rows
    val Date: ImageVector get() = PhosphorIcons.Regular.CalendarBlank
    val Time: ImageVector get() = PhosphorIcons.Regular.Clock
    val Duration: ImageVector get() = PhosphorIcons.Regular.Hourglass
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
