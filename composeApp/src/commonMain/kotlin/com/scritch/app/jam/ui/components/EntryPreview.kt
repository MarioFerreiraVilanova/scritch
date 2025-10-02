package com.scritch.app.jam.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.scritch.app.jam.ModerationStatus
import com.scritch.app.theme.scritchColorScheme
import com.scritch.app.theme.scritchShapes
import com.scritch.app.theme.scritchTypography
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.delete
import scritch.composeapp.generated.resources.rejected
import scritch.composeapp.generated.resources.status_approved
import scritch.composeapp.generated.resources.status_pending

@Composable
fun EntryPreview(
    imageUrl: String,
    moderationStatus: ModerationStatus,
    onRetakeImage: () -> Unit,
    onDeleteImage: () -> Unit,
    onModerationStatusClick: () -> Unit,
    isJamExpired: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clip(MaterialTheme.shapes.medium),
        contentAlignment = Alignment.BottomEnd,
    ) {
        // Real image (fades/scales in over the skeleton)
        KamelImage(
            resource = { asyncPainterResource(imageUrl) },
            contentDescription = null,
            contentScale = ContentScale.Crop,
            animationSpec = tween(600),
            onLoading = { progress ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f) // keep space reserved; adjust if you prefer 4/3, 3/2, etc.
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            onFailure = { exception ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(MaterialTheme.colorScheme.errorContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(48.dp)
                    )
                }
            },
            modifier = Modifier.aspectRatio(1f)
        )

        // Action buttons only after image is visible
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ){
            Surface(
                onClick = onModerationStatusClick,
                modifier = Modifier.padding(16.dp),
                shape = CircleShape,
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    Icon(
                        imageVector = when (moderationStatus){
                            ModerationStatus.Pending -> Icons.Default.Info
                            ModerationStatus.Approved -> Icons.Default.CheckCircle
                            ModerationStatus.Rejected -> Icons.Default.Error
                        },
                        contentDescription = null,
                    )
                    Text(
                        modifier = Modifier.padding(end = 4.dp),
                        text = when (moderationStatus){
                            ModerationStatus.Pending -> stringResource(Res.string.status_pending)
                            ModerationStatus.Approved -> stringResource(Res.string.status_approved)
                            ModerationStatus.Rejected -> stringResource(Res.string.rejected)
                        },
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilledTonalIconButton(
                    onClick = onDeleteImage,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(Res.string.delete)
                    )
                }
                FilledIconButton(
                    onClick = onRetakeImage,
                    enabled = !isJamExpired
                ) {
                    Icon(imageVector = Icons.Default.Repeat, contentDescription = null)
                }
            }
        }
    }
}

@Preview
@Composable
private fun EntryPreviewPendingPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        EntryPreview(
            imageUrl = "https://example.com/image.jpg",
            moderationStatus = ModerationStatus.Pending,
            onRetakeImage = {},
            onDeleteImage = {},
            onModerationStatusClick = {},
            isJamExpired = false,
        )
    }
}

@Preview
@Composable
private fun EntryPreviewApprovedPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        EntryPreview(
            imageUrl = "https://example.com/image.jpg",
            moderationStatus = ModerationStatus.Approved,
            onRetakeImage = {},
            onDeleteImage = {},
            onModerationStatusClick = {},
            isJamExpired = false,
        )
    }
}

@Preview
@Composable
private fun EntryPreviewRejectedPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        EntryPreview(
            imageUrl = "https://example.com/image.jpg",
            moderationStatus = ModerationStatus.Rejected,
            onRetakeImage = {},
            onDeleteImage = {},
            onModerationStatusClick = {},
            isJamExpired = false,
        )
    }
}