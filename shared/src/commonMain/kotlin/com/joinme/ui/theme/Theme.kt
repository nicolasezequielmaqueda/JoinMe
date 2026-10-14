package com.joinme.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

/**
 * Theme of the app. Our own colours are read with JoinMeTheme.colors.
 * MaterialTheme is still configured so Material components
 * (dialogs, date picker, text fields) use the same colours.
 */
@Composable
fun JoinMeTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors

    val base = if (colors.isDark) darkColorScheme() else lightColorScheme()
    val materialColors = base.copy(
        primary = colors.accent,
        onPrimary = colors.onAccent,
        primaryContainer = colors.accentSoft,
        onPrimaryContainer = colors.accent,
        secondaryContainer = colors.accentSoft,
        onSecondaryContainer = colors.accent,
        background = colors.background,
        onBackground = colors.textPrimary,
        surface = colors.surface,
        onSurface = colors.textPrimary,
        onSurfaceVariant = colors.textSecondary,
        surfaceContainerHigh = colors.surface,
        surfaceContainerHighest = colors.fill,
        outline = colors.separator,
        outlineVariant = colors.separator,
        inverseSurface = colors.textPrimary,
        inverseOnSurface = colors.background,
    )

    CompositionLocalProvider(LocalJoinMeColors provides colors) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = joinMeTypography(),
            content = content,
        )
    }
}

/** Short way to read the colours: JoinMeTheme.colors.accent */
object JoinMeTheme {
    val colors: JoinMeColors
        @Composable @ReadOnlyComposable
        get() = LocalJoinMeColors.current
}
