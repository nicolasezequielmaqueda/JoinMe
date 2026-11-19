package com.joinme.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.theme.JoinMeDimens
import com.joinme.ui.theme.JoinMeTheme
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

/**
 * Big title at the start of the main screens, like the iOS "Large Title".
 * It scrolls with the content and expects the 16 dp side margin of the screen.
 * [overline] is a small text above the title (e.g. today's date) and [action]
 * a button at the end of the title line. With [centered] everything is centred (login).
 */
@Composable
fun LargeTitleHeader(
    title: String,
    modifier: Modifier = Modifier,
    overline: String? = null,
    centered: Boolean = false,
    action: @Composable (() -> Unit)? = null,
) {
    val textAlign = if (centered) TextAlign.Center else TextAlign.Start
    Column(
        horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, top = 8.dp, bottom = 4.dp),
    ) {
        if (overline != null) {
            Text(
                text = overline.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = JoinMeTheme.colors.textSecondary,
                textAlign = textAlign,
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = JoinMeTheme.colors.textPrimary,
                textAlign = textAlign,
                modifier = Modifier.weight(1f, fill = !centered).semantics { heading() },
            )
            if (action != null) action()
        }
    }
}

/**
 * Bar at the top of every screen after the login: "‹ Back" on the left (if [onBack] is set),
 * a small centred title and an optional [action] on the right, like the iOS navigation bar.
 * It floats over the content. When [scrolled] it becomes glass, so the content passes under it.
 */
@Composable
fun NavBar(
    title: String,
    scrolled: Boolean,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    backLabel: String = "Back",
    action: @Composable (() -> Unit)? = null,
) {
    val colors = JoinMeTheme.colors
    // Without animations the surface simply appears.
    val glassAmount by animateFloatAsState(
        targetValue = if (scrolled) 1f else 0f,
        animationSpec = tween(durationMillis = 150),
    )

    Box(modifier = modifier.fillMaxWidth()) {
        if (glassAmount > 0f) {
            GlassSurface(
                kind = GlassKind.TopBar,
                modifier = Modifier.matchParentSize().graphicsLayer { alpha = glassAmount },
                content = {},
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .heightIn(min = 56.dp),
        ) {
            if (onBack != null) {
                TextButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp).heightIn(min = 48.dp),
                ) {
                    Icon(
                        imageVector = AppIcons.Back,
                        contentDescription = null,
                        tint = colors.accent,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(backLabel, style = MaterialTheme.typography.bodyLarge, color = colors.accent)
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = if (onBack != null) 104.dp else 112.dp)
                    .then(if (title.isEmpty()) Modifier else Modifier.semantics { heading() }),
            )
            if (action != null) {
                Box(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp)) { action() }
            }
        }
    }
}

/**
 * Puts [navBar] over [content]. The content fills the screen, so it can pass under the bar,
 * and receives the height of the bar to leave that room at its start.
 */
@Composable
fun NavBarLayout(
    navBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (barHeight: Dp) -> Unit,
) {
    val density = LocalDensity.current
    var barHeight by remember { mutableStateOf(0.dp) }

    Box(modifier = modifier.fillMaxSize()) {
        content(barHeight)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onSizeChanged { barHeight = with(density) { it.height.toDp() } },
        ) {
            navBar()
        }
    }
}

/**
 * Small grey title above a group of rows, e.g. "WHEN". With [large] it is the bigger
 * title of a list group (e.g. "Tomorrow"). [count] is shown at the end of the line.
 */
@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier, large: Boolean = false, count: Int? = null) {
    val colors = JoinMeTheme.colors
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = JoinMeDimens.spaceL,
                end = JoinMeDimens.spaceL,
                top = if (large) JoinMeDimens.spaceXL else JoinMeDimens.sectionGap,
                bottom = 6.dp,
            ),
    ) {
        Text(
            text = if (large) text else text.uppercase(),
            style = if (large) MaterialTheme.typography.titleLarge else MaterialTheme.typography.labelSmall,
            color = if (large) colors.textPrimary else colors.textSecondary,
            modifier = Modifier.weight(1f).semantics { heading() },
        )
        if (count != null) {
            Text(text = "$count", style = MaterialTheme.typography.bodyMedium, color = colors.textSecondary)
        }
    }
}
