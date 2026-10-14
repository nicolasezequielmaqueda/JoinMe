package com.joinme.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.joinme.model.Activity
import com.joinme.ui.theme.JoinMeTheme
import com.joinme.ui.theme.categoryColors

/**
 * Thin progress bar that shows how full the activity is.
 * The numbers are always written next to it, so it is only visual help.
 */
@Composable
fun ParticipantsBar(activity: Activity, modifier: Modifier = Modifier) {
    val progress = (activity.participantCount.toFloat() / activity.maxParticipants).coerceIn(0f, 1f)
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
                .background(categoryColors(activity.type.category).content, CircleShape),
        )
    }
}
