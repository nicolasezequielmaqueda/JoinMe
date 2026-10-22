package com.joinme.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.joinme.ui.theme.JoinMeTheme

/**
 * Rounded background that groups several information rows.
 */
@Composable
fun GroupedSection(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(JoinMeTheme.colors.surface),
    ) {
        content()
    }
}

/**
 * One row of a GroupedSection: icon, label on the left and value on the right.
 * The value uses the remaining width and can wrap onto several lines.
 */
@Composable
fun InfoRow(icon: ImageVector, label: String, value: String, showDivider: Boolean = true) {
    val colors = JoinMeTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .semantics(mergeDescendants = true) { },
    ) {
        Icon(icon, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textPrimary,
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textSecondary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
    if (showDivider) {
        // Inset divider that starts after the icon, like in iOS.
        HorizontalDivider(color = colors.separator, modifier = Modifier.padding(start = 48.dp))
    }
}
