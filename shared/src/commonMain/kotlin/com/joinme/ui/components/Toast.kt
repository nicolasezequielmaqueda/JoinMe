package com.joinme.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.theme.JoinMeDimens
import com.joinme.ui.theme.JoinMeTheme
import kotlinx.coroutines.delay
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

private const val TOAST_MILLIS = 2_500L

/**
 * The message of the toast. One instance is shared by the whole app, so a screen can
 * show a toast and leave (Create → Detail) while the next screen still shows it.
 */
@Stable
class ToastState {
    var message by mutableStateOf("")
        private set
    var isWarning by mutableStateOf(false)
        private set
    var visible by mutableStateOf(false)
        private set

    // Changes with every toast, so a new message restarts the 2.5 s timer.
    var showCount by mutableIntStateOf(0)
        private set

    fun show(message: String, isWarning: Boolean = false) {
        this.message = message
        this.isWarning = isWarning
        visible = true
        showCount++
    }

    fun hide() {
        visible = false
    }
}

val LocalToastState = staticCompositionLocalOf<ToastState> { error("LocalToastState not provided") }

/** Shows the toast of [state] as a glass pill. Put it over the content, above the bottom bar. */
@Composable
fun ToastHost(state: ToastState, modifier: Modifier = Modifier) {
    // The state is shared by the whole app, so a toast must not survive the screen that shows it.
    DisposableEffect(Unit) { onDispose { state.hide() } }

    LaunchedEffect(state.showCount) {
        if (!state.visible) return@LaunchedEffect
        delay(TOAST_MILLIS)
        state.hide()
    }

    AnimatedVisibility(
        visible = state.visible,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier,
    ) {
        GlassSurface(kind = GlassKind.FloatingBar) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = JoinMeDimens.spaceL, vertical = JoinMeDimens.spaceM)
                    .semantics { liveRegion = LiveRegionMode.Polite },
            ) {
                Icon(
                    imageVector = if (state.isWarning) AppIcons.Warning else AppIcons.CheckCircle,
                    contentDescription = null,
                    tint = if (state.isWarning) JoinMeTheme.colors.warning else JoinMeTheme.colors.accent,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(JoinMeDimens.spaceS))
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = JoinMeTheme.colors.textPrimary,
                )
            }
        }
    }
}
