package com.scritch.app.jam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.scritch.app.prompt.Prompt
import com.scritch.app.prompt.TipsSheet
import com.scritch.app.uicomponents.Button
import com.scritch.app.util.dayOfWeekString
import io.github.ismoy.imagepickerkmp.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.ImagePickerLauncher
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.camera
import scritch.composeapp.generated.resources.clock
import scritch.composeapp.generated.resources.share_your_work
import scritch.composeapp.generated.resources.weekly_jam_description
import scritch.composeapp.generated.resources.weekly_jam_end_time
import scritch.composeapp.generated.resources.weekly_jam_not_available

@Composable
fun JamScreen(
    modifier: Modifier = Modifier,
    viewModel: JamViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()


    Scaffold(
        modifier = modifier,
    ) { contentPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .consumeWindowInsets(contentPadding)
                .padding(contentPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.weekly_jam_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            viewState.endDate?.let { endDate ->

                val dayOfWeek = dayOfWeekString(day = endDate.dayOfWeek)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.clock),
                        contentDescription = null,
                    )
                    Text(
                        text = stringResource(
                            Res.string.weekly_jam_end_time,
                            dayOfWeek,
                            "${endDate.hour}:${endDate.minute}"
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
            HorizontalDivider()
            when (viewState.loadingState) {
                LoadingState.LOADING -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                LoadingState.NO_JAM -> {
                    Text(
                        text = stringResource(Res.string.weekly_jam_not_available),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }

                LoadingState.LOADED,
                LoadingState.REFRESHING -> {
                    Prompt(
                        viewState = viewState.promptViewState,
                        onCategoryClick = viewModel::onCategoryClick,
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (viewState.loadingState == LoadingState.LOADED) {
                SubmissionButtons(
                    viewState = viewState.submissionState,
                    onSubmitWork = viewModel::onSubmitWork,
                    onRemoveSubmission = viewModel::onRemoveSubmission,
                    onShowPreview = viewModel::onShowPreview,
                )
            }
        }
    }

    if (viewState.showCamera) {
        ImagePickerLauncher(
            config = ImagePickerConfig(
                onPhotoCaptured = viewModel::onImageCaptured,
                onError = viewModel::onImageCaptureError,
                onDismiss = viewModel::onImageCaptureDismiss,
            )
        )
    }

    TipsSheet(
        viewState = viewState.promptViewState,
        onTipDisplayed = viewModel::onTipDisplayed,
    )

    when (viewState.dialog){
        JamScreenDialog.SubmissionPreview -> {
            (viewState.submissionState as? SubmissionViewState.ImageTaken)?.let { submission ->
                SubmissionPreviewDialog(
                    viewState = submission,
                    onDismissRequest = viewModel::onDismissDialog,
                    onRemoveSubmission = viewModel::onRemoveSubmission,
                )
            }
        }
        null -> {}
    }
}

@Composable
private fun SubmissionButtons (
    viewState: SubmissionViewState,
    onSubmitWork: () -> Unit,
    onRemoveSubmission: () -> Unit,
    onShowPreview: () -> Unit,
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            8.dp,
            Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (viewState){
            is SubmissionViewState.ImageTaken -> {
                FilledIconButton(
                    onClick = onShowPreview,
                ){
                    KamelImage(
                        resource = {
                            asyncPainterResource(viewState.image.uri)
                        },
                        contentDescription = null,
                        modifier = Modifier.scale(1.5f)
                    )
                }
                FilledIconButton(
                    onClick = onSubmitWork
                ) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = null,
                    )
                }
                IconButton(
                    onClick = onRemoveSubmission,
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                    )
                }
            }
            SubmissionViewState.NotSubmitted -> {
                Button(
                    onClick = onSubmitWork
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.camera),
                        contentDescription = null,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = stringResource(Res.string.share_your_work))
                }
            }
        }
    }
}

@Composable
private fun SubmissionPreviewDialog (
    viewState: SubmissionViewState.ImageTaken,
    onDismissRequest: () -> Unit,
    onRemoveSubmission: () -> Unit,
){
    Dialog(
        onDismissRequest = onDismissRequest,
    ){
        KamelImage(
            resource = {
                asyncPainterResource(viewState.image.uri)
            },
            contentDescription = null,
        )
    }
}
