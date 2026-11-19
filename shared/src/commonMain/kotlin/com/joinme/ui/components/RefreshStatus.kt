package com.joinme.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.joinme.data.SupabaseErrors
import com.joinme.ui.theme.JoinMeTheme

/** Text for the line above the lists when the last refresh failed, or null if it worked. */
fun refreshStatusText(refreshError: String?, lastUpdated: String?): String? {
    if (refreshError == null) return null

    val offline = refreshError == SupabaseErrors.OFFLINE
    return when {
        offline && lastUpdated == null -> "Offline"
        offline -> "Offline · Last updated $lastUpdated"
        lastUpdated == null -> "Could not refresh."
        else -> "Could not refresh. Showing saved activities from $lastUpdated"
    }
}

/** Discreet line shown only when the last refresh failed. */
@Composable
fun RefreshStatusLine(refreshError: String?, lastUpdated: String?, modifier: Modifier = Modifier) {
    val text = refreshStatusText(refreshError, lastUpdated) ?: return

    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = JoinMeTheme.colors.textSecondary,
        modifier = modifier.padding(start = 4.dp, end = 4.dp, bottom = 8.dp),
    )
}
