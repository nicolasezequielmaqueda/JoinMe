package com.joinme.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.joinme.model.ActivityStatus
import com.joinme.ui.theme.ToneColors
import com.joinme.ui.theme.joinedColors
import com.joinme.ui.theme.statusColors

/** Small rounded label with the status written as text, e.g. "Open". */
@Composable
fun StatusPill(status: ActivityStatus, modifier: Modifier = Modifier) {
    Pill(text = status.label, colors = statusColors(status), modifier = modifier)
}

/** Label shown when the current user takes part in the activity. */
@Composable
fun JoinedPill(modifier: Modifier = Modifier) {
    Pill(text = "JOINED", colors = joinedColors(), modifier = modifier)
}

@Composable
private fun Pill(text: String, colors: ToneColors, modifier: Modifier) {
    Text(
        text = text,
        color = colors.content,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .background(colors.background, CircleShape)
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .semantics { contentDescription = "Status: $text" },
    )
}
