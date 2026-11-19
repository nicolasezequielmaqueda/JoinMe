
package com.joinme.ui.screens.detail

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.joinme.model.Activity
import com.joinme.ui.components.ActionRow
import com.joinme.ui.components.GroupedSection
import com.joinme.ui.components.SectionHeader
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.theme.JoinMeTheme
import com.joinme.util.DateTimeUtils
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

@Composable
fun ActivityInvite(activity: Activity, shareText: (String) -> Boolean) {
    val time = DateTimeUtils.formatTime(activity.startTime)
    val invite = "${activity.title}\n${activity.date} at $time\n" +
        "${activity.location.displayName}\nJoinMe demo activity"
    var dialogTitle by rememberSaveable { mutableStateOf<String?>(null) }

    SectionHeader("Invite")
    GroupedSection {
        ActionRow(
            text = "Share activity",
            icon = AppIcons.Share,
            color = JoinMeTheme.colors.accent,
            onClick = { if (!shareText(invite)) dialogTitle = "Sharing is unavailable" },
        )
        ActionRow(
            text = "Show invite text",
            icon = AppIcons.InviteText,
            color = JoinMeTheme.colors.accent,
            onClick = { dialogTitle = "Invite text" },
            showDivider = false,
        )
    }

    dialogTitle?.let { title ->
        AlertDialog(
            onDismissRequest = { dialogTitle = null },
            title = { Text(title) },
            text = {
                SelectionContainer {
                    Text(invite)
                }
            },
            confirmButton = {
                TextButton(onClick = { dialogTitle = null }) { Text("Close") }
            },
        )
    }
}
