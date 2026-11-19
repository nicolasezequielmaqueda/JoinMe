package com.joinme.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.joinme.ui.theme.CardShape
import com.joinme.ui.theme.JoinMeDimens
import com.joinme.ui.theme.JoinMeTheme

// 16 (margin) + 30 (icon tile) + 12 (gap): the divider starts under the text, not under the icon.
private val DividerStartWithIcon = 58.dp

/**
 * Rounded background that groups several rows, like a group in iOS Settings.
 */
@Composable
fun GroupedSection(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(JoinMeTheme.colors.surface),
    ) {
        content()
    }
}

/** Small coloured square with a white icon at the start of a row, like iOS Settings. */
@Composable
private fun RowIcon(icon: ImageVector, tint: Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(30.dp).background(tint, RoundedCornerShape(8.dp)),
    ) {
        Icon(icon, contentDescription = null, tint = JoinMeTheme.colors.onAccent, modifier = Modifier.size(18.dp))
    }
}

/**
 * One row of a GroupedSection: optional icon, label on the left and value on the right.
 * When the value is too long for the line it moves below the label.
 */
@Composable
fun InfoRow(label: String, value: String, icon: ImageVector? = null, showDivider: Boolean = true) {
    val colors = JoinMeTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = JoinMeDimens.minTouchTarget)
            .padding(horizontal = JoinMeDimens.spaceL, vertical = JoinMeDimens.spaceM)
            .semantics(mergeDescendants = true) { },
    ) {
        if (icon != null) {
            RowIcon(icon, colors.accent)
            Spacer(Modifier.width(JoinMeDimens.spaceM))
        }
        FlowRow(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f),
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge, color = colors.textPrimary)
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                textAlign = TextAlign.End,
            )
        }
    }
    if (showDivider) RowDivider(start = if (icon != null) DividerStartWithIcon else JoinMeDimens.spaceL)
}

/**
 * A tappable row of a GroupedSection with an icon and a text, like "Sign out" in iOS Settings.
 * The colour tells the kind of action (accent, or red for destructive ones).
 */
@Composable
fun ActionRow(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    enabled: Boolean = true,
    showDivider: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val tint = if (enabled) color else JoinMeTheme.colors.textSecondary
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(interactionSource)
            .heightIn(min = JoinMeDimens.minTouchTarget)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = JoinMeDimens.spaceL, vertical = JoinMeDimens.spaceM),
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(JoinMeDimens.spaceM))
        Text(text = text, style = MaterialTheme.typography.bodyLarge, color = tint)
    }
    if (showDivider) RowDivider(start = 50.dp)
}

/** Inset divider between rows. */
@Composable
fun RowDivider(start: Dp = JoinMeDimens.spaceL) {
    HorizontalDivider(color = JoinMeTheme.colors.separator, modifier = Modifier.padding(start = start))
}

/** Small grey explanation below a GroupedSection. */
@Composable
fun SectionFooter(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = JoinMeTheme.colors.textSecondary,
        modifier = modifier.padding(horizontal = JoinMeDimens.spaceL).padding(top = 6.dp),
    )
}
