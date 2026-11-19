
package com.joinme.ui.screens.detail

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import com.joinme.data.Weather
import com.joinme.data.WeatherSource
import com.joinme.ui.components.SectionFooter
import com.joinme.ui.components.GroupedSection
import com.joinme.ui.components.SectionHeader
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.theme.JoinMeDimens
import com.joinme.ui.theme.JoinMeTheme
import com.joinme.util.DateTimeUtils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import androidx.compose.runtime.getValue

data class WeatherUiState(
    val weather: Weather?,
    val status: String,
    val isLoading: Boolean = false,
)

class WeatherViewModel(
    private val source: WeatherSource,
    private val date: LocalDate,
    private val time: LocalTime,
) : ViewModel() {
    private val state = MutableStateFlow(
        source.saved(date, time).let { saved ->
            if (saved == null) WeatherUiState(null, "Check the forecast for the start of the activity.")
            else WeatherUiState(saved, "Saved forecast. Refresh to update it.")
        },
    )
    val uiState = state.asStateFlow()

    fun refresh() {
        val previous = state.value
        if (previous.isLoading) return
        state.value = previous.copy(status = "Loading forecast…", isLoading = true)
        viewModelScope.launch {
            state.value = try {
                val weather = source.fetch(date, time)
                if (weather == null) {
                    previous.copy(status = "No forecast data was returned. You can try again.")
                } else {
                    WeatherUiState(weather, "Forecast updated.")
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                previous.copy(status = "Could not load the forecast. Check your connection and try again.")
            }
        }
    }
}

private const val WEATHER_NOTE = "Łódź (demo) · Open-Meteo"

/** Forecast for the hour the activity starts. */
@Composable
fun WeatherPanel(source: WeatherSource, date: LocalDate, time: LocalTime) {
    val unavailable = forecastUnavailableText(date, time, DateTimeUtils.now())

    SectionHeader(weatherTitle(date, time, DateTimeUtils.today()))
    if (unavailable == null) {
        ForecastCard(source, date, time)
    } else {
        GroupedSection {
            Text(
                text = unavailable,
                style = MaterialTheme.typography.bodyMedium,
                color = JoinMeTheme.colors.textSecondary,
                modifier = Modifier.padding(JoinMeDimens.spaceL),
            )
        }
        SectionFooter(WEATHER_NOTE)
    }
}

@Composable
private fun ForecastCard(source: WeatherSource, date: LocalDate, time: LocalTime) {
    val model = viewModel { WeatherViewModel(source, date, time) }
    val state by model.uiState.collectAsStateWithLifecycle()
    val colors = JoinMeTheme.colors
    val reading = state.weather

    GroupedSection {
        Column(Modifier.padding(start = JoinMeDimens.spaceL, top = JoinMeDimens.spaceM, bottom = JoinMeDimens.spaceM)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (reading != null) {
                    WeatherIcon(rainy = reading.precipitation > 0.0 || reading.rainChance >= 50)
                    Spacer(Modifier.width(JoinMeDimens.spaceM))
                    Text(
                        text = "${reading.temperature} °C",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp, lineHeight = 34.sp),
                        color = colors.textPrimary,
                    )
                    Text(
                        text = rainText(reading.rainChance, reading.precipitation),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f).padding(start = JoinMeDimens.spaceM),
                    )
                } else {
                    Text(
                        text = state.status,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                        modifier = Modifier.weight(1f).semantics { liveRegion = LiveRegionMode.Polite },
                    )
                }
                RefreshButton(
                    description = if (reading == null) "Check forecast" else "Refresh forecast",
                    isLoading = state.isLoading,
                    onClick = model::refresh,
                )
            }
            if (reading != null) {
                Text(
                    text = state.status,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    modifier = Modifier
                        .padding(end = JoinMeDimens.spaceL, top = JoinMeDimens.spaceXS)
                        .semantics { liveRegion = LiveRegionMode.Polite },
                )
            }
            if (reading != null && isOutdated(reading.updated, DateTimeUtils.now())) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = JoinMeDimens.spaceS, end = JoinMeDimens.spaceL),
                ) {
                    Icon(AppIcons.Warning, contentDescription = null, tint = colors.warning, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("May be out of date", style = MaterialTheme.typography.bodySmall, color = colors.warning)
                }
            }
        }
    }
    SectionFooter(
        if (reading == null) WEATHER_NOTE
        else "Łódź (demo) · Updated ${readingClock(reading.updated)} · Open-Meteo",
    )
}

@Composable
private fun WeatherIcon(rainy: Boolean) {
    val colors = JoinMeTheme.colors
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(40.dp).background(colors.accentSoft, RoundedCornerShape(JoinMeDimens.radiusM)),
    ) {
        Icon(
            imageVector = if (rainy) AppIcons.Rainy else AppIcons.Sunny,
            contentDescription = null,
            tint = colors.accent,
            modifier = Modifier.size(24.dp),
        )
    }
}

/** 48 dp icon button that turns while the weather is loading. */
@Composable
private fun RefreshButton(description: String, isLoading: Boolean, onClick: () -> Unit) {
    // The animation only exists while loading, so it does not run for nothing.
    val rotation = if (isLoading) {
        rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(durationMillis = 1_000, easing = LinearEasing)),
        ).value
    } else {
        0f
    }
    IconButton(onClick = onClick, enabled = !isLoading) {
        Icon(
            imageVector = AppIcons.Refresh,
            contentDescription = description,
            tint = if (isLoading) JoinMeTheme.colors.textSecondary else JoinMeTheme.colors.accent,
            modifier = Modifier.size(24.dp).graphicsLayer { rotationZ = rotation },
        )
    }
}
