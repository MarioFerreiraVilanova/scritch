package com.scritch.app.jam.ui.components.dialogs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import scritch.composeapp.generated.resources.submission_preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmissionPreviewDialog(
    imageUrl: String,
    isUserSubmission: Boolean,
    moderationStatus: ModerationStatus?,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KamelImage(
                resource = { asyncPainterResource(imageUrl) },
                contentDescription = stringResource(Res.string.submission_preview),
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        min = 160.dp,
                        max = 420.dp
                    ),
                onLoading = { progress ->
                    // progress in [0f..1f] or null
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = 160.dp, max = 420.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterVertically
                        )
                    ) {
                        val animatedProgress by animateFloatAsState(progress)
                        CircularProgressIndicator(
                            progress = {
                                animatedProgress
                            }
                        )
                        Text(
                            text = "Loading...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun SubmissionPreviewDialogPreview() {
    MaterialTheme(
        colorScheme = scritchColorScheme,
        typography = scritchTypography(),
        shapes = scritchShapes,
    ) {
        SubmissionPreviewDialog(
            imageUrl = "https://example.com/image.jpg",
            isUserSubmission = true,
            moderationStatus = ModerationStatus.Approved,
            onDismissRequest = {},
        )
    }
}