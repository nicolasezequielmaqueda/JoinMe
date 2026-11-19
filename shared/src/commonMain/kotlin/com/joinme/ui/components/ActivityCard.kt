package com.joinme.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.joinme.model.ActivityStatus
import com.joinme.ui.icons.AppIcons
import com.joinme.ui.theme.CardShape
import com.joinme.ui.theme.JoinMeDimens
import com.joinme.ui.theme.JoinMeTheme
import com.joinme.ui.theme.cardShadow
import com.joinme.ui.theme.placesColor
import com.joinme.util.DateTimeUtils

/**
 * One activity in a list. The whole card is clickable and opens the detail.
 * Without [onClick] it is only a picture (the preview of the Create screen).
 * [showDate] is false in lists already grouped by day. [highlighted] marks the next plan.
 */
@Composable
fun ActivityCard(
    item: ActivityItem,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    showDate: Boolean = true,
    highlighted: Boolean = false,
) {
    val activity = item.activity
    val colors = JoinMeTheme.colors
    val interactionSource = remember { MutableInteractionSource() }

    val finished = item.status == ActivityStatus.CANCELLED || item.status == ActivityStatus.COMPLETED
    val contentAlpha = if (finished) 0.6f else 1f
    val free = activity.maxParticipants - activity.participantCount
    val places = placesText(item.status, activity.participantCount, activity.maxParticipants)
    val time = DateTimeUtils.formatTime(activity.startTime)
    val place = activity.location.displayName

    val description = "${activity.title}, ${DateTimeUtils.formatDate(activity.date)} $time, $place, $places, " +
        (if (item.isJoined && !item.isCreator && !finished) "joined" else item.status.label.lowercase()) +
        (if (highlighted) ", next up" else "")

    // pressScale goes first so the shadow and the border scale with the card.
    var card = modifier.fillMaxWidth()
    if (onClick != null) card = card.pressScale(interactionSource)
    card = card
        .cardShadow()
        .clip(CardShape)
        .background(colors.surface)
    if (highlighted) card = card.border(1.5.dp, colors.accent, CardShape)
    if (onClick != null) {
        card = card.clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            role = Role.Button,
            onClick = onClick,
        )
    }

    Column(
        modifier = card
            .padding(JoinMeDimens.spaceL)
            .semantics(mergeDescendants = true) { contentDescription = description },
    ) {
        Row {
            ActivityIconTile(activity.type, modifier = Modifier.alpha(contentAlpha))
            Spacer(Modifier.width(JoinMeDimens.spaceM))
            Column(modifier = Modifier.weight(1f)) {
                // With a very big font the pill moves below the title instead of squeezing the text.
                FlowRow(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    itemVerticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.alpha(contentAlpha).padding(end = JoinMeDimens.spaceS)) {
                        if (highlighted) {
                            Text(
                                text = "NEXT UP",
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.accent,
                            )
                        }
                        Text(
                            text = activity.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    // JOINED says more than OPEN, so it takes its place.
                    if (item.isJoined && !item.isCreator && !finished) JoinedPill() else StatusPill(item.status)
                }
                // The icon stays on the first line when the text wraps.
                Row(modifier = Modifier.alpha(contentAlpha)) {
                    Icon(
                        imageVector = AppIcons.Time,
                        contentDescription = null,
                        tint = colors.textTertiary,
                        modifier = Modifier.padding(top = 3.dp).size(14.dp),
                    )
                    Spacer(Modifier.width(JoinMeDimens.spaceXS))
                    Text(
                        text = if (showDate) "${DateTimeUtils.formatDate(activity.date)} · $time · $place" else "$time · $place",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary,
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = JoinMeDimens.spaceM).alpha(contentAlpha),
        ) {
            PlacesIndicator(activity, color = placesColor(free), modifier = Modifier.weight(1f))
            Spacer(Modifier.width(JoinMeDimens.spaceM))
            Text(
                text = places,
                style = MaterialTheme.typography.bodySmall,
                color = if (item.status == ActivityStatus.OPEN) placesColor(free) else colors.textSecondary,
            )
        }
    }
}
