package com.joinme.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.joinme.model.ActivityStatus
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.theme.JoinMeDimens
import com.joinme.ui.theme.ToneColors
import com.joinme.ui.theme.joinedColors
import com.joinme.ui.theme.statusColors

/** Small rounded label with the status written as text, e.g. "OPEN" (with a dot). */
@Composable
fun StatusPill(status: ActivityStatus, modifier: Modifier = Modifier) {
    val colors = statusColors(status)
    Pill(text = status.label, colors = colors, modifier = modifier) {
        if (status == ActivityStatus.OPEN) {
            Box(Modifier.size(6.dp).background(colors.content, CircleShape))
        }
    }
}

/** Label shown when the current user takes part in the activity. */
@Composable
fun JoinedPill(modifier: Modifier = Modifier) {
    val colors = joinedColors()
    Pill(text = "JOINED", colors = colors, modifier = modifier) {
        Icon(AppIcons.Check, contentDescription = null, tint = colors.content, modifier = Modifier.size(12.dp))
    }
}

@Composable
private fun Pill(text: String, colors: ToneColors, modifier: Modifier, leading: @Composable () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(colors.background, JoinMeDimens.radiusFull)
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .semantics { contentDescription = "Status: $text" },
    ) {
        leading()
        Spacer(Modifier.width(4.dp))
        Text(text = text, color = colors.content, style = MaterialTheme.typography.labelMedium)
    }
}
