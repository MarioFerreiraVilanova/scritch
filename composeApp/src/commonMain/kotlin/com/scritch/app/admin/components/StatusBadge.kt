package com.scritch.app.admin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scritch.app.jam.data.JamStatus
import org.jetbrains.compose.resources.stringResource
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.jam_status_finished
import scritch.composeapp.generated.resources.jam_status_ongoing
import scritch.composeapp.generated.resources.jam_status_planned

@Composable
fun StatusBadge(
    status: JamStatus,
    modifier: Modifier = Modifier,
) {
    val (text, backgroundColor, textColor) = when (status) {
        JamStatus.Planned -> Triple(
            stringResource(Res.string.jam_status_planned),
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        )
        JamStatus.Ongoing -> Triple(
            stringResource(Res.string.jam_status_ongoing),
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        JamStatus.Finished -> Triple(
            stringResource(Res.string.jam_status_finished),
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Text(
        text = text,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp
        ),
        color = textColor
    )
}