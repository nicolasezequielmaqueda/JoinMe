
package com.joinme

import android.app.UiModeManager
import android.content.ActivityNotFoundException
import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var notificationsAllowed by mutableStateOf(true)
    private var glassEnabled by mutableStateOf(false)
    private var askedForNotifications = false

    // The result is ignored on purpose: if the user says no, the app works the same.
    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            notificationsAllowed = NotificationManagerCompat.from(this).areNotificationsEnabled()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        askedForNotifications = savedInstanceState?.getBoolean(KEY_ASKED_NOTIFICATIONS) ?: false
        val session = ViewModelProvider(this)[JoinMeSession::class.java]

        // Asked after sign-in, when reminders make sense, instead of on the login screen.
        lifecycleScope.launch {
            session.auth.userId.first { it != null }
            askForNotifications()
        }

        setContent {
            App(
                session.repository,
                session.weather,
                ::shareActivity,
                session.auth,
                notificationsAllowed,
                glassEnabled,
            )
        }
    }

    override fun onResume() {
        super.onResume()
        notificationsAllowed = NotificationManagerCompat.from(this).areNotificationsEnabled()
        glassEnabled = canUseGlass()
    }

    // Blur needs Android 12. With high contrast or animations removed the bars stay opaque.
    // "high_text_contrast_enabled" has no public constant, but any app can read it.
    private fun canUseGlass(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return false

        val animationsRemoved =
            Settings.Global.getFloat(contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
        val highTextContrast =
            Settings.Secure.getInt(contentResolver, "high_text_contrast_enabled", 0) == 1
        val highContrastTheme = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
            getSystemService(UiModeManager::class.java).contrast >= 0.5f
        return !animationsRemoved && !highTextContrast && !highContrastTheme
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_ASKED_NOTIFICATIONS, askedForNotifications)
    }

    private fun askForNotifications() {
        if (askedForNotifications) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (notificationsAllowed) return

        askedForNotifications = true
        notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun shareActivity(text: String): Boolean {
        val message = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        return try {
            startActivity(Intent.createChooser(message, "Share activity"))
            true
        } catch (_: ActivityNotFoundException) {
            false
        } catch (_: SecurityException) {
            false
        }
    }

    private companion object {
        const val KEY_ASKED_NOTIFICATIONS = "asked_notifications"
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
