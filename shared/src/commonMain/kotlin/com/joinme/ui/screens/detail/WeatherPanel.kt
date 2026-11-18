
package com.joinme.ui.screens.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewModelScope
import com.joinme.data.Weather
import com.joinme.data.WeatherSource
import com.joinme.ui.components.GroupedSection
import com.joinme.ui.components.SectionHeader
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeatherUiState(
    val weather: Weather?,
    val status: String,
    val isLoading: Boolean = false,
)

class WeatherViewModel(private val source: WeatherSource) : ViewModel() {
    private val state = MutableStateFlow(
        WeatherUiState(source.saved(), "Check the current weather before going outside."),
    )
    val uiState = state.asStateFlow()

    fun refresh() {
        val previous = state.value
        if (previous.isLoading) return
        state.value = previous.copy(status = "Loading weather…", isLoading = true)
        viewModelScope.launch {
            state.value = try {
                val weather = source.fetch()
                if (weather == null) {
                    previous.copy(status = "No weather data was returned. You can try again.")
                } else {
                    WeatherUiState(weather, "Weather updated.")
                }
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                previous.copy(status = "Could not load weather. Check your connection and try again.")
            }
        }
    }
}

@Composable
fun WeatherPanel(source: WeatherSource) {
    val model = viewModel { WeatherViewModel(source) }
    val state by model.uiState.collectAsStateWithLifecycle()

    SectionHeader("Outdoor weather")
    GroupedSection {
        Column(Modifier.padding(16.dp)) {
            Text("Łódź, Poland (demo location)", style = MaterialTheme.typography.titleSmall)
            Text(state.status, style = MaterialTheme.typography.bodyMedium)
            state.weather?.let {
                Text("${it.temperature} °C · ${it.precipitation} mm precipitation")
                Text(
                    "Saved reading: ${it.time.replace('T', ' ')} local time. It may be out of date.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Text("Weather data: Open-Meteo", style = MaterialTheme.typography.bodySmall)
            TextButton(onClick = model::refresh, enabled = !state.isLoading) {
                Text(if (state.weather == null) "Check weather" else "Refresh weather")
            }
        }
    }
}
