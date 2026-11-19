package com.joinme.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.joinme.ui.theme.JoinMeDimens
import com.joinme.ui.theme.JoinMeTheme

/**
 * Friendly message shown instead of an empty list, so the screen is never blank.
 * With [isLoading] a spinner replaces the icon. [actionText] adds a button below the message.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    actionText: String? = null,
    onAction: () -> Unit = {},
) {
    val colors = JoinMeTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = JoinMeDimens.spaceXXXL, vertical = JoinMeDimens.spaceXXXL),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(72.dp).background(colors.accentSoft, CircleShape),
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = colors.accent, strokeWidth = 3.dp, modifier = Modifier.size(32.dp))
            } else {
                Icon(icon, contentDescription = null, tint = colors.accent, modifier = Modifier.size(32.dp))
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = JoinMeDimens.spaceL),
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 280.dp).padding(top = JoinMeDimens.spaceXS),
        )
        if (actionText != null) {
            CompactButton(
                text = actionText,
                onClick = onAction,
                modifier = Modifier.padding(top = JoinMeDimens.spaceM),
            )
        }
    }
}
