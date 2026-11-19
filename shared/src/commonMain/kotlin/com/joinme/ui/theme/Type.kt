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
        headlineLarge = style(34, FontWeight.Bold, 41, -0.4),   // large screen title
        headlineMedium = style(22, FontWeight.Bold, 28),        // detail title
        titleLarge = style(20, FontWeight.Bold, 25),            // day group headers, empty states
        titleMedium = style(17, FontWeight.SemiBold, 22),       // card title, headline
        bodyLarge = style(17, FontWeight.Normal, 22),
        bodyMedium = style(15, FontWeight.Normal, 20),          // subhead
        bodySmall = style(13, FontWeight.Normal, 18),           // footnote
        labelLarge = style(17, FontWeight.SemiBold, 22),        // buttons
        labelMedium = style(12, FontWeight.SemiBold, 16),       // pills, captions
        labelSmall = style(13, FontWeight.SemiBold, 18, 0.3),   // section headers (uppercase)
    )
}
