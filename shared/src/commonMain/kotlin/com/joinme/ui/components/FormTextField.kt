package com.joinme.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.theme.JoinMeDimens
import com.joinme.ui.theme.JoinMeTheme
import androidx.compose.runtime.getValue

/**
 * Text field for the rows of a GroupedSection: small label above the value and,
 * while it has the focus, a 2 dp accent line at the bottom.
 */
@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    singleLine: Boolean,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val colors = JoinMeTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val accent = colors.accent

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .drawBehind {
                if (focused) {
                    // Inset so the line does not follow the rounded corners of the section.
                    val y = size.height - 1.dp.toPx()
                    val inset = JoinMeDimens.spaceL.toPx()
                    drawLine(accent, Offset(inset, y), Offset(size.width - inset, y), strokeWidth = 2.dp.toPx())
                }
            }
            .heightIn(min = 56.dp)
            .padding(horizontal = JoinMeDimens.spaceL, vertical = JoinMeDimens.spaceS),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = colors.textSecondary)
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                minLines = if (singleLine) 1 else 3,
                keyboardOptions = keyboardOptions,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.textPrimary),
                cursorBrush = SolidColor(colors.accent),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(placeholder, style = MaterialTheme.typography.bodyLarge, color = colors.textSecondary)
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (trailingIcon != null) trailingIcon()
    }
}

/** Error text with an icon, so the problem never depends only on the red colour. */
@Composable
fun FieldError(text: String, modifier: Modifier = Modifier) {
    val color = JoinMeTheme.colors.destructive
    Row(verticalAlignment = Alignment.Top, modifier = modifier.fillMaxWidth()) {
        Icon(AppIcons.WarningCircle, contentDescription = null, tint = color, modifier = Modifier.size(16.dp).padding(top = 1.dp))
        Spacer(Modifier.width(JoinMeDimens.spaceS))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = color)
    }
}
