package com.joinme.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.joinme.model.Activity
import com.joinme.ui.theme.JoinMeDimens
import com.joinme.ui.theme.JoinMeTheme

// Up to this many places every place is drawn as a dot; above it a bar is clearer.
private const val MAX_DOTS = 6

/**
 * Shows how full an activity is: one dot per place, or a bar when there are many.
 * The numbers are always written next to it, so it is only visual help.
 */
@Composable
fun PlacesIndicator(activity: Activity, color: Color, modifier: Modifier = Modifier) {
    val taken = activity.participantCount
    val capacity = activity.maxParticipants

    if (capacity <= MAX_DOTS) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(JoinMeDimens.spaceXS),
            modifier = modifier.clearAndSetSemantics { },
        ) {
            repeat(capacity) { index ->
                val dot = Modifier.size(10.dp)
                Box(
                    modifier = if (index < taken) dot.background(color, CircleShape)
                    else dot.border(1.5.dp, color, CircleShape),
                )
            }
        }
    } else {
        val progress = (taken.toFloat() / capacity).coerceIn(0f, 1f)
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(JoinMeTheme.colors.fill, CircleShape)
                .clearAndSetSemantics { },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(color, CircleShape),
            )
        }
    }
}
