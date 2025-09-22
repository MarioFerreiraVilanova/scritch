package com.scritch.app.admin.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.scritch.app.jam.data.JamDto
import org.jetbrains.compose.resources.stringResource
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.delete_jam
import scritch.composeapp.generated.resources.edit_jam
import scritch.composeapp.generated.resources.jam_admin_actions
import scritch.composeapp.generated.resources.recalculate_stats
import scritch.composeapp.generated.resources.recalculate_stats_description

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JamAdminActionsBottomSheet(
    jam: JamDto,
    onDismiss: () -> Unit,
    onEditJam: () -> Unit,
    onDeleteJam: () -> Unit,
    onRecalculateStats: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.jam_admin_actions),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = jam.id,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            AdminActionItem(
                icon = Icons.Default.Edit,
                title = stringResource(Res.string.edit_jam),
                description = "Modify jam details and settings",
                onClick = {
                    onEditJam()
                    onDismiss()
                }
            )

            AdminActionItem(
                icon = Icons.Default.Refresh,
                title = stringResource(Res.string.recalculate_stats),
                description = stringResource(Res.string.recalculate_stats_description),
                onClick = {
                    onRecalculateStats()
                    onDismiss()
                }
            )

            AdminActionItem(
                icon = Icons.Default.Delete,
                title = stringResource(Res.string.delete_jam),
                description = "Permanently delete this jam and all submissions",
                onClick = {
                    onDeleteJam()
                    onDismiss()
                },
                isDestructive = true
            )
        }
    }
}

@Composable
private fun AdminActionItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (isDestructive) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDestructive) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}