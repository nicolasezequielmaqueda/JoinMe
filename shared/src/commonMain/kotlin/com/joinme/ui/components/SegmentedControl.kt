package com.joinme.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.joinme.ui.theme.JoinMeTheme

/**
 * A row of options with a rounded background around the selected option.
 */
@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = JoinMeTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.fill)
            .padding(3.dp)
            .selectableGroup(),
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 40.dp)
                    .then(if (selected) Modifier.shadow(1.dp, RoundedCornerShape(8.dp)) else Modifier)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selected) colors.surface else Color.Transparent)
                    .selectable(selected = selected, role = Role.Tab, onClick = { onSelect(index) }),
            ) {
                Text(
                    text = option,
                    style = if (selected) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyMedium,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                )
            }
        }
    }
}
