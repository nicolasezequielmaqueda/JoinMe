package com.joinme.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import joinme.shared.generated.resources.Res
import joinme.shared.generated.resources.inter_bold
import joinme.shared.generated.resources.inter_medium
import joinme.shared.generated.resources.inter_regular
import joinme.shared.generated.resources.inter_semibold
import org.jetbrains.compose.resources.Font

/** Inter font (open source, similar to the iOS system font). */
@Composable
fun interFontFamily(): FontFamily = FontFamily(
    Font(Res.font.inter_regular, FontWeight.Normal),
    Font(Res.font.inter_medium, FontWeight.Medium),
    Font(Res.font.inter_semibold, FontWeight.SemiBold),
    Font(Res.font.inter_bold, FontWeight.Bold),
)

/**
 * Text styles with sizes similar to iOS (Large Title, Headline, Body...).
 * All sizes are in "sp", so they grow with the system font size.
 */
@Composable
fun joinMeTypography(): Typography {
    val inter = interFontFamily()
    fun style(size: Int, weight: FontWeight, lineHeight: Int, spacing: Double = 0.0) = TextStyle(
        fontFamily = inter,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        letterSpacing = spacing.sp,
    )
    return Typography(
        headlineLarge = style(32, FontWeight.Bold, 38, -0.5),   // large screen title
        headlineMedium = style(26, FontWeight.Bold, 32, -0.4),  // detail title
        titleLarge = style(20, FontWeight.SemiBold, 26, -0.2),
        titleMedium = style(17, FontWeight.SemiBold, 22, -0.2), // card title
        bodyLarge = style(17, FontWeight.Normal, 22, -0.2),
        bodyMedium = style(15, FontWeight.Normal, 20, -0.1),
        bodySmall = style(13, FontWeight.Normal, 18),
        labelLarge = style(16, FontWeight.SemiBold, 21, -0.1), // buttons
        labelMedium = style(13, FontWeight.SemiBold, 16),       // pills
        labelSmall = style(11, FontWeight.Medium, 13, 0.1),     // tab bar, small caps
    )
}
