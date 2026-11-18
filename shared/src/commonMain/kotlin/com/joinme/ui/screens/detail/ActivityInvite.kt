
package com.joinme.ui.screens.detail

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.joinme.model.Activity
import com.joinme.util.DateTimeUtils

@Composable
fun ActivityInvite(activity: Activity, shareText: (String) -> Boolean) {
    val time = DateTimeUtils.formatTime(activity.startTime)
    val invite = "${activity.title}\n${activity.date} at $time\n" +
        "${activity.location.displayName}\nJoinMe demo activity"
    var dialogTitle by rememberSaveable { mutableStateOf<String?>(null) }

    FlowRow {
        TextButton(onClick = {
            if (!shareText(invite)) dialogTitle = "Sharing is unavailable"
        }) {
            Text("Share activity")
        }
        TextButton(onClick = { dialogTitle = "Invite text" }) {
            Text("Show invite text")
        }
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
