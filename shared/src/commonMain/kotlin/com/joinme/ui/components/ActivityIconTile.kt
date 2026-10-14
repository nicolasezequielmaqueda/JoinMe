package com.joinme.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.joinme.model.ActivityType
import com.joinme.ui.icons.icon
import com.joinme.ui.theme.categoryColors

/**
 * Rounded square with the icon of the activity type, in the soft colour
 * of its category (like the app icons in iOS Settings).
 * It is decorative: the activity name is always shown next to it.
 */
@Composable
fun ActivityIconTile(type: ActivityType, size: Dp = 48.dp, modifier: Modifier = Modifier) {
    val colors = categoryColors(type.category)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .background(colors.background, RoundedCornerShape(size * 0.28f)),
    ) {
        Icon(
            imageVector = type.icon,
            contentDescription = null,
            tint = colors.content,
            modifier = Modifier.size(size * 0.5f),
        )
    }
}
