package com.joinme.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.joinme.ui.theme.JoinMeDimens
import com.joinme.ui.theme.JoinMeTheme
import androidx.compose.runtime.getValue

/**
 * Main action of a screen (Join, Create). While [isLoading] it shows a spinner and
 * [loadingText] (e.g. "Joining…") and ignores taps. Callers choose the width.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    loadingText: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    val colors = JoinMeTheme.colors
    AppButton(text, loadingText, onClick, modifier, enabled, isLoading, background = colors.accent, content = colors.onAccent)
}

/** Secondary action (Leave). Soft background with accent text. */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    loadingText: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    val colors = JoinMeTheme.colors
    AppButton(text, loadingText, onClick, modifier, enabled, isLoading, background = colors.accentSoft, content = colors.accent)
}

@Composable
private fun AppButton(
    text: String,
    loadingText: String,
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    isLoading: Boolean,
    background: Color,
    content: Color,
) {
    val colors = JoinMeTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    Button(
        onClick = onClick,
        // Taps are ignored while the action is running.
        enabled = enabled && !isLoading,
        shape = JoinMeDimens.radiusFull,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = content,
            disabledContainerColor = if (isLoading) background else colors.fill,
            disabledContentColor = if (isLoading) content else colors.textSecondary,
        ),
        elevation = null,
        modifier = modifier
            .heightIn(min = JoinMeDimens.buttonHeight)
            .pressScale(interactionSource)
            .alpha(if (pressed) 0.85f else 1f),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = LocalContentColor.current,
                strokeWidth = 2.dp,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(10.dp))
        }
        Text(if (isLoading) loadingText else text, style = MaterialTheme.typography.labelLarge)
    }
}

/** Small button for secondary actions next to a title or inside an empty state, e.g. "New". */
@Composable
fun CompactButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    val colors = JoinMeTheme.colors
    val interactionSource = remember { MutableInteractionSource() }

    // The button is 36 dp high but the tappable area keeps the 48 dp minimum.
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .pressScale(interactionSource)
            .heightIn(min = JoinMeDimens.compactButtonHeight)
            .alpha(if (enabled) 1f else 0.5f)
            .clip(JoinMeDimens.radiusFull)
            .background(colors.accentSoft)
            .clickable(enabled = enabled, interactionSource = interactionSource, indication = LocalIndication.current, role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = colors.accent, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(text, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = colors.accent)
    }
}
