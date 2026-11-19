package com.joinme.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/** Sizes shared by all screens, so margins and corners look the same everywhere. */
object JoinMeDimens {
    // Corners: small (fields), medium (icon tiles), large (cards and grouped sections).
    val radiusS = 10.dp
    val radiusM = 14.dp
    val radiusL = 22.dp
    val radiusFull = CircleShape // buttons, chips and pills

    val spaceXS = 4.dp
    val spaceS = 8.dp
    val spaceM = 12.dp
    val spaceL = 16.dp
    val spaceXL = 20.dp
    val spaceXXL = 24.dp
    val spaceXXXL = 32.dp

    val screenGutter = 16.dp
    val cardGap = 12.dp
    val sectionGap = 28.dp

    val buttonHeight = 52.dp
    val compactButtonHeight = 36.dp
    val minTouchTarget = 48.dp
}

val CardShape = RoundedCornerShape(JoinMeDimens.radiusL)

/** Soft shadow for cards. In dark mode the surface colour already separates the card. */
@Composable
fun Modifier.cardShadow(shape: Shape = CardShape): Modifier =
    if (JoinMeTheme.colors.isDark) this else shadow(
        elevation = 2.dp,
        shape = shape,
        ambientColor = Color.Black.copy(alpha = 0.04f),
        spotColor = Color.Black.copy(alpha = 0.06f),
    )
