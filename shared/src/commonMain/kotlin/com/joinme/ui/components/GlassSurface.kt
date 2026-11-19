package com.joinme.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.joinme.ui.theme.JoinMeTheme
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur

/**
 * True when the "glass" look can be used. It is calculated by the platform:
 * on Android it is false below Android 12 (no blur) and when the user asks for
 * high contrast or no animations. Then every glass surface is opaque.
 */
val LocalGlassEnabled = staticCompositionLocalOf { false }

/** Shared by the content that goes under glass surfaces and by the surfaces themselves. */
val LocalHazeState = staticCompositionLocalOf<HazeState> { error("LocalHazeState not provided") }

/** Height of the floating tab bar, so lists can leave room under their last item. 0 without tab bar. */
val LocalTabBarHeight = compositionLocalOf { 0.dp }

// Enough tint so text keeps its contrast whatever passes below the glass.
private const val BarTintAlpha = 0.9f
private const val FloatingTintAlpha = 0.7f

/** Where a glass surface is used. It decides the shape and the edge line. */
enum class GlassKind { FloatingBar, TopBar, BottomBar }

/** Content that goes under glass surfaces (lists, scrolling screens) must use this. */
@Composable
fun Modifier.glassSource(): Modifier =
    if (LocalGlassEnabled.current) hazeSource(LocalHazeState.current) else this

/**
 * Surface that blurs what is behind it, like the iOS bars. Without glass it is
 * a plain surface with a separator line, so text is always readable.
 */
@Composable
fun GlassSurface(
    kind: GlassKind,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = JoinMeTheme.colors
    val glass = LocalGlassEnabled.current
    val shape = if (kind == GlassKind.FloatingBar) CircleShape else RectangleShape
    val tintAlpha = if (kind == GlassKind.FloatingBar) FloatingTintAlpha else BarTintAlpha

    var surface = modifier
    if (kind == GlassKind.FloatingBar) {
        surface = surface.shadow(elevation = 16.dp, shape = shape, ambientColor = Color.Black, spotColor = Color.Black)
    }
    surface = surface.clip(shape)
    surface = if (glass) {
        val style = HazeBlurStyle {
            blurRadius(32.dp)
            backgroundColor(colors.background)
            colorEffects(listOf(HazeColorEffect.tint(colors.surface.copy(alpha = tintAlpha))))
        }
        surface.hazeBlur(HazeInput.Sources(LocalHazeState.current), style)
    } else {
        surface.background(colors.surface)
    }

    surface = when (kind) {
        GlassKind.FloatingBar -> {
            // A bright thin edge, like the border of iOS glass.
            val edge = if (!glass) colors.separator else if (colors.isDark) Color.White.copy(alpha = 0.22f) else Color.White
            surface.border(1.dp, Brush.verticalGradient(listOf(edge, edge.copy(alpha = edge.alpha * 0.3f))), shape)
        }
        GlassKind.TopBar -> surface.edgeLine(colors.separator, atBottom = true)
        GlassKind.BottomBar -> surface.edgeLine(colors.separator, atBottom = false)
    }

    Box(modifier = surface, content = content)
}

private fun Modifier.edgeLine(color: Color, atBottom: Boolean): Modifier = drawBehind {
    val y = if (atBottom) size.height else 0f
    drawLine(color, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
}
