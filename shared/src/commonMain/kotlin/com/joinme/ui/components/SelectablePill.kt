package com.joinme.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.joinme.ui.theme.JoinMeDimens
import com.joinme.ui.theme.JoinMeTheme

/**
 * Rounded option that can be selected (filters, duration, place).
 * The selected option is dark (light in dark mode) and exposes its selected state.
 */
@Composable
fun SelectablePill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    val colors = JoinMeTheme.colors
    val background = if (selected) colors.textPrimary else colors.fillTertiary
    val content = if (selected) colors.background else colors.textPrimary
    val interactionSource = remember { MutableInteractionSource() }

    // The outer box gives the 48 dp tap area; the pill itself is 34 dp high.
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .heightIn(min = JoinMeDimens.minTouchTarget)
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.RadioButton,
                onClick = onClick,
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .pressScale(interactionSource)
                .heightIn(min = 34.dp)
                .clip(JoinMeDimens.radiusFull)
                .background(background)
                .padding(horizontal = 14.dp),
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = content, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = content,
            )
        }
    }
}
