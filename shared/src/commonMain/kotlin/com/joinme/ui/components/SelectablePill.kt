package com.joinme.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.joinme.ui.theme.JoinMeTheme

/**
 * Rounded option that can be selected (filters, duration, location).
 * The selected option uses the accent colour and exposes its selected state.
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
    val background = if (selected) colors.accent else colors.surface
    val content = if (selected) colors.onAccent else colors.textPrimary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .heightIn(min = 40.dp)
            .clip(CircleShape)
            .background(background)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = content, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = content)
    }
}
