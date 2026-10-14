package com.joinme.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Colours of JoinMe. Calm and neutral, inspired by iOS:
 * grey background, white cards and one soft accent colour.
 *
 * Text colours keep a contrast of at least 4.5:1 with the background.
 */
@Immutable
data class JoinMeColors(
    val background: Color,     // screen background
    val surface: Color,        // cards and grouped sections
    val fill: Color,           // grey fills: segmented control, stepper...
    val textPrimary: Color,
    val textSecondary: Color,
    val separator: Color,
    val accent: Color,         // buttons, selected items, links
    val onAccent: Color,
    val accentSoft: Color,     // light background for accent elements
    val isDark: Boolean,
)

val LightColors = JoinMeColors(
    background = Color(0xFFF2F2F7),
    surface = Color(0xFFFFFFFF),
    fill = Color(0xFFE9E9EE),
    textPrimary = Color(0xFF1C1C1E),
    textSecondary = Color(0xFF6B6B70),
    separator = Color(0xFFE0E0E5),
    accent = Color(0xFF35507A),
    onAccent = Color(0xFFFFFFFF),
    accentSoft = Color(0xFFE6EBF3),
    isDark = false,
)

val DarkColors = JoinMeColors(
    background = Color(0xFF000000),
    surface = Color(0xFF1C1C1E),
    fill = Color(0xFF2C2C2E),
    textPrimary = Color(0xFFF2F2F7),
    textSecondary = Color(0xFFAEAEB2),
    separator = Color(0xFF38383A),
    accent = Color(0xFFA9BFE6),
    onAccent = Color(0xFF14233D),
    accentSoft = Color(0xFF26324A),
    isDark = true,
)

val LocalJoinMeColors = staticCompositionLocalOf { LightColors }
