package com.joinme.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.joinme.model.ActivityCategory
import com.joinme.model.ActivityStatus

/** A soft background with a darker colour on top (for icons or text). */
data class ToneColors(val background: Color, val content: Color)

/**
 * Each category always has the same soft colour, so the user learns it:
 * sports = sage, games = sand, social = lavender, other = blue grey.
 */
@Composable
fun categoryColors(category: ActivityCategory): ToneColors {
    val dark = JoinMeTheme.colors.isDark
    return when (category) {
        ActivityCategory.SPORTS ->
            if (dark) ToneColors(Color(0xFF1F2D25), Color(0xFF9CCBAE)) else ToneColors(Color(0xFFE4EFE8), Color(0xFF3F7454))
        ActivityCategory.GAMES ->
            if (dark) ToneColors(Color(0xFF30281C), Color(0xFFE2C08F)) else ToneColors(Color(0xFFF4ECDF), Color(0xFF8A622E))
        ActivityCategory.SOCIAL ->
            if (dark) ToneColors(Color(0xFF2A2536), Color(0xFFC3B5E3)) else ToneColors(Color(0xFFECE8F4), Color(0xFF65578C))
        ActivityCategory.OTHER ->
            if (dark) ToneColors(Color(0xFF212A35), Color(0xFFA9BCD6)) else ToneColors(Color(0xFFE6ECF3), Color(0xFF4A6588))
    }
}

/**
 * Colours of the status pills. Colour only helps: the status is always written too.
 */
@Composable
fun statusColors(status: ActivityStatus): ToneColors {
    val dark = JoinMeTheme.colors.isDark
    return when (status) {
        ActivityStatus.OPEN ->
            if (dark) ToneColors(Color(0xFF1E3326), Color(0xFF8FD4A6)) else ToneColors(Color(0xFFE3F1E7), Color(0xFF2D6B43))
        ActivityStatus.FULL ->
            if (dark) ToneColors(Color(0xFF3A2C17), Color(0xFFF0C47E)) else ToneColors(Color(0xFFFAEEDB), Color(0xFF8A5300))
        ActivityStatus.CANCELLED ->
            if (dark) ToneColors(Color(0xFF3A1F1F), Color(0xFFF2A8A3)) else ToneColors(Color(0xFFFBE6E4), Color(0xFFA3271F))
        ActivityStatus.COMPLETED ->
            ToneColors(JoinMeTheme.colors.fill, JoinMeTheme.colors.textSecondary)
    }
}

/** Colours of the "JOINED" pill: the accent colour of the app. */
@Composable
fun joinedColors(): ToneColors =
    ToneColors(JoinMeTheme.colors.accentSoft, JoinMeTheme.colors.accent)

/** Red used only for destructive actions (cancel an activity). */
@Composable
fun destructiveColor(): Color =
    if (JoinMeTheme.colors.isDark) Color(0xFFFF6961) else Color(0xFFB3261E)
