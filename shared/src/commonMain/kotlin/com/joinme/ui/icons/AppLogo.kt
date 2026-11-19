package com.joinme.ui.icons

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.unit.dp

/**
 * Logo of the app: three people under a roof on a blue background. It is the same
 * drawing as the launcher icon of the Android app, so both always look alike.
 */
val AppLogo: ImageVector by lazy {
    // The viewport is the launcher canvas (108) cropped to 80, so the people fill the square.
    ImageVector.Builder(
        name = "AppLogo",
        defaultWidth = 80.dp,
        defaultHeight = 80.dp,
        viewportWidth = 80f,
        viewportHeight = 80f,
    ).apply {
        group(translationX = -14f, translationY = -14f) {
            addPath(
                pathData = PathParser().parsePathString("M0,0h108v108h-108z").toNodes(),
                fill = Brush.linearGradient(
                    colors = listOf(Color(0xFF4A6A9C), Color(0xFF2B4266)),
                    start = Offset(0f, 0f),
                    end = Offset(108f, 108f),
                ),
            )
            addPath(
                pathData = PathParser().parsePathString("M26,52L54,30L82,52").toNodes(),
                stroke = SolidColor(Color.White),
                strokeLineWidth = 5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
            addPath(
                pathData = PathParser().parsePathString(SIDE_PEOPLE).toNodes(),
                fill = SolidColor(Color.White.copy(alpha = 0.75f)),
            )
            addPath(
                pathData = PathParser().parsePathString(CENTER_PERSON).toNodes(),
                fill = SolidColor(Color.White),
            )
            addPath(
                pathData = PathParser().parsePathString(ROOF_DOT).toNodes(),
                fill = SolidColor(Color(0xFFF6C453)),
            )
        }
    }.build()
}

private const val SIDE_PEOPLE =
    "M33,61a5,5 0,1 0,10 0a5,5 0,1 0,-10 0ZM29,80Q29,69 38,69Q47,69 47,80Z" +
        "M65,61a5,5 0,1 0,10 0a5,5 0,1 0,-10 0ZM61,80Q61,69 70,69Q79,69 79,80Z"

private const val CENTER_PERSON =
    "M47.5,55a6.5,6.5 0,1 0,13 0a6.5,6.5 0,1 0,-13 0ZM42,80Q42,66 54,66Q66,66 66,80Z"

private const val ROOF_DOT = "M51.4,40a2.6,2.6 0,1 0,5.2 0a2.6,2.6 0,1 0,-5.2 0Z"
