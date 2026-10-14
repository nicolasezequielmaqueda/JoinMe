package com.joinme.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.joinme.ui.theme.JoinMeTheme
import com.joinme.ui.theme.destructiveColor

private val ButtonShape = RoundedCornerShape(14.dp)

/** Main action of a screen (Join, Create). Full width and easy to tap. */
@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val colors = JoinMeTheme.colors
    AppButton(text, onClick, modifier, enabled, background = colors.accent, content = colors.onAccent)
}

/** Secondary action (Leave). Soft background with accent text. */
@Composable
fun SecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val colors = JoinMeTheme.colors
    AppButton(text, onClick, modifier, enabled, background = colors.accentSoft, content = colors.accent)
}

/** Action that cannot be undone (Cancel activity). Red text on a soft background. */
@Composable
fun DestructiveButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val red = destructiveColor()
    AppButton(text, onClick, modifier, enabled, background = red.copy(alpha = 0.10f), content = red)
}

@Composable
private fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    background: Color,
    content: Color,
) {
    val colors = JoinMeTheme.colors
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = ButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = content,
            disabledContainerColor = colors.fill,
            disabledContentColor = colors.textSecondary,
        ),
        elevation = null,
        modifier = modifier.fillMaxWidth().heightIn(min = 52.dp),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}
