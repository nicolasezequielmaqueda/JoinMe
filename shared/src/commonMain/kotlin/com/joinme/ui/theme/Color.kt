package com.joinme.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Colours of JoinMe. Calm and neutral, inspired by iOS:
 * grey background, white cards and one soft accent colour.
 *
 * Text and background colour pairs still need a full contrast check.
 */
@Immutable
data class JoinMeColors(
    val background: Color,     // screen background
    val surface: Color,        // cards and grouped sections
    val surfaceElevated: Color, // dialogs, over the surface
    val fill: Color,           // grey fills: segmented control, stepper...
    val fillTertiary: Color,   // unselected chips
    val fillSelected: Color,   // selected option over a fill: white in light, raised grey in dark
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,   // only for non-essential text, its contrast is lower
    val separator: Color,
    val accent: Color,         // buttons, selected items, links
    val onAccent: Color,
    val accentSoft: Color,     // light background for accent elements
    val destructive: Color,    // cancel and delete actions
    val warning: Color,        // text of warnings (few places left, old data)
    val warningSoft: Color,    // background behind a warning
    val isDark: Boolean,
)

val LightColors = JoinMeColors(
    background = Color(0xFFF2F2F7),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFFFFFFF),
    fill = Color(0xFFE9E9EE),
    fillTertiary = Color(0xFFE3E3E8),
    fillSelected = Color(0xFFFFFFFF),
    textPrimary = Color(0xFF1C1C1E),
    textSecondary = Color(0xFF6B6B70),
    textTertiary = Color(0xFF8A8A8E),
    separator = Color(0xFFE0E0E5),
    accent = Color(0xFF35507A),
    onAccent = Color(0xFFFFFFFF),
    accentSoft = Color(0xFFE6EBF3),
    destructive = Color(0xFFB3261E),
    warning = Color(0xFF8A5300),
    warningSoft = Color(0xFFFAEEDB),
    isDark = false,
)

val DarkColors = JoinMeColors(
    background = Color(0xFF000000),
    surface = Color(0xFF1C1C1E),
    surfaceElevated = Color(0xFF2C2C2E),
    fill = Color(0xFF2C2C2E),
    fillTertiary = Color(0xFF2C2C2E),
    fillSelected = Color(0xFF636366),
    textPrimary = Color(0xFFF2F2F7),
    textSecondary = Color(0xFFAEAEB2),
    textTertiary = Color(0xFF8E8E93),
    separator = Color(0xFF38383A),
    accent = Color(0xFFA9BFE6),
    onAccent = Color(0xFF14233D),
    accentSoft = Color(0xFF26324A),
    destructive = Color(0xFFFF6961),
    warning = Color(0xFFF0C47E),
    warningSoft = Color(0xFF3A2C17),
    isDark = true,
)

val LocalJoinMeColors = staticCompositionLocalOf { LightColors }
