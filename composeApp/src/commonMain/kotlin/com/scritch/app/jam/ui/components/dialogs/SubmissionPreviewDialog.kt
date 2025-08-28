package com.scritch.app.jam.ui.components.dialogs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.scritch.app.jam.ModerationStatus
import com.scritch.app.jam.SubmissionViewState
import com.scritch.app.theme.scritchColorScheme
import com.scritch.app.theme.scritchShapes
import com.scritch.app.theme.scritchTypography
import com.scritch.app.uicomponents.Button
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.submission_preview

@Composable
fun SubmissionPreviewDialog(
    viewState: SubmissionViewState.Submitted,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false, // so we can control size on large screens
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        )
    ) {
        // Full-screen box just to center the card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp) // cap width for tablets
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState()), // safety if content gets tall
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KamelImage(
                        resource = { asyncPainterResource(viewState.imageUrl) },
                        contentDescription = stringResource(Res.string.submission_preview),
                        contentScale = ContentScale.FillWidth,        // don't fill; keep aspect
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(
                                min = 160.dp,
                                max = 420.dp
                            ), // cap height so button stays visible
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

                    Button(onClick = onDismissRequest) {
                        Text("Ok")
                    }
                }
            }
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
            viewState = SubmissionViewState.Submitted(
                imageUrl = "https://example.com/image.jpg",
                moderationStatus = ModerationStatus.Approved
            ),
            onDismissRequest = {}
        )
    }
}