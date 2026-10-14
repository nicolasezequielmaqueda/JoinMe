package com.joinme.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.theme.JoinMeTheme

/**
 * Big title at the top of the main screens, like the iOS "Large Title".
 * [overline] is a small text above the title (e.g. today's date).
 */
@Composable
fun LargeTitleHeader(
    title: String,
    modifier: Modifier = Modifier,
    overline: String? = null,
    action: @Composable (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            if (overline != null) {
                Text(
                    text = overline.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = JoinMeTheme.colors.textSecondary,
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = JoinMeTheme.colors.textPrimary,
                modifier = Modifier.semantics { heading() },
            )
        }
        if (action != null) action()
    }
}

/**
 * Top bar of the secondary screens: "‹ Back" on the left and a small
 * centred title, like the iOS navigation bar.
 */
@Composable
fun NavBar(title: String, onBack: () -> Unit, backLabel: String = "Back") {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .heightIn(min = 52.dp),
    ) {
        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp),
        ) {
            Icon(
                imageVector = AppIcons.Back,
                contentDescription = null,
                tint = JoinMeTheme.colors.accent,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(4.dp))
            Text(backLabel, style = MaterialTheme.typography.bodyLarge, color = JoinMeTheme.colors.accent)
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = JoinMeTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 96.dp)
                .semantics { heading() },
        )
    }
}

/** Small grey title above a group of rows, e.g. "WHEN". */
@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = JoinMeTheme.colors.textSecondary,
        modifier = modifier
            .padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
            .semantics { heading() },
    )
}
