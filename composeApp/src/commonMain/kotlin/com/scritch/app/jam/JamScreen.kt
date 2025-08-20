package com.scritch.app.jam

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Try
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.scritch.app.prompt.Prompt
import com.scritch.app.prompt.TipsSheet
import com.scritch.app.uicomponents.Button
import io.github.ismoy.imagepickerkmp.GalleryPickerLauncher
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.until
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.are_you_sure_delete_entry
import scritch.composeapp.generated.resources.camera
import scritch.composeapp.generated.resources.cancel
import scritch.composeapp.generated.resources.clock
import scritch.composeapp.generated.resources.delete
import scritch.composeapp.generated.resources.delete_entry
import scritch.composeapp.generated.resources.edit_your_entry
import scritch.composeapp.generated.resources.ends_in_d_h
import scritch.composeapp.generated.resources.ends_in_h_m
import scritch.composeapp.generated.resources.ends_in_m_s
import scritch.composeapp.generated.resources.ends_in_s
import scritch.composeapp.generated.resources.pick_one_from_the_gallery
import scritch.composeapp.generated.resources.share_your_work
import scritch.composeapp.generated.resources.submission_preview
import scritch.composeapp.generated.resources.take_a_different_picture
import scritch.composeapp.generated.resources.take_a_picture
import scritch.composeapp.generated.resources.upload_complete
import scritch.composeapp.generated.resources.upload_failed
import scritch.composeapp.generated.resources.uploading_your_image
import scritch.composeapp.generated.resources.weekly_jam_description
import scritch.composeapp.generated.resources.weekly_jam_not_available
import scritch.composeapp.generated.resources.yes_delete_it
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun JamScreen(
    chosenImagePath: String?,
    onOpenCamera: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: JamViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()

    LaunchedEffect(chosenImagePath) {
        viewModel.onImageCaptured(chosenImagePath ?: return@LaunchedEffect)
    }

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
                JamEndCountdown(
                    endDate = endDate
                )
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

            (viewState.submissionState as? SubmissionViewState.Submitted)?.let {
                EntryPreview(
                    viewState = it,
                    onRetakeImage = viewModel::onSubmitWork,
                    onDeleteImage = viewModel::onRemoveSubmission,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (viewState.loadingState == LoadingState.LOADED) {
                SubmissionActions(
                    viewState = viewState.submissionState,
                    onSubmitWork = viewModel::onSubmitWork,
                    onRetry = viewModel::onRetryUpload,
                )
            }
        }
    }

    TipsSheet(
        viewState = viewState.promptViewState,
        onTipDisplayed = viewModel::onTipDisplayed,
    )

    ImageSourcePickerSheet(
        viewState = viewState,
        onCameraSelected = {
            viewModel.onCameraSelectedAsSource()
            onOpenCamera()
        },
        onGallerySelected = viewModel::onGallerySelectedAsSource,
        onDismissRequest = {
            viewModel.onDismissDialog(JamScreenDialog.ImageSourceSheet)
        }
    )

    when (viewState.dialog) {
        JamScreenDialog.SubmissionDeleteConfirmation -> SubmissionDeleteConfirmationDialog(
            onDismissRequest = {
                viewModel.onDismissDialog(JamScreenDialog.SubmissionDeleteConfirmation)
            },
            onConfirm = viewModel::onRemoveSubmission,
        )

        JamScreenDialog.ImageSourceSheet,
        null -> {
        }

        JamScreenDialog.GalleryPicker -> {
            GalleryPickerLauncher(
                allowMultiple = false,
                onPhotosSelected = { images ->
                    images.firstOrNull()?.let { chosenImage ->
                        viewModel.onImageCaptured(chosenImage.uri)
                    }
                },
                onDismiss = {
                    viewModel.onDismissDialog(JamScreenDialog.GalleryPicker)
                },
                onError = { _ ->
                    // TODO handle the error, maybe show a snack bar or something
                    viewModel.onDismissDialog(JamScreenDialog.GalleryPicker)
                }
            )
        }
    }
}

@Composable
fun EntryPreview(
    viewState: SubmissionViewState.Submitted,
    onRetakeImage: () -> Unit,
    onDeleteImage: () -> Unit,
){
    Box (
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomEnd,
    ){
        KamelImage(
            resource = { asyncPainterResource(viewState.imageUrl) },
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
        )

        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ){
            FilledIconButton(
                onClick = onDeleteImage,
            ){
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                )
            }
            FilledIconButton(
                onClick = onRetakeImage,
            ){
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = null,
                )
            }
        }
    }
}


@OptIn(ExperimentalTime::class)
@Composable
fun JamEndCountdown(
    endDate: LocalDateTime, // if you have Instant, see note below
    timeZone: TimeZone = TimeZone.currentSystemDefault()
) {
    fun remainingSeconds(): Long {
        val end = endDate.toInstant(timeZone)
        val now = Clock.System.now()
        return now.until(end, DateTimeUnit.SECOND)
    }

    @Composable
    fun format(sec: Long): String {
        if (sec <= 0) return "Ended"

        val days = sec / 86_400
        val hours = (sec % 86_400) / 3_600
        val minutes = (sec % 3_600) / 60
        val seconds = sec % 60

        return when {
            days >= 1 -> stringResource(Res.string.ends_in_d_h, days, hours)
            sec >= 3_600 -> stringResource(Res.string.ends_in_h_m, hours, minutes)
            sec >= 60 -> stringResource(Res.string.ends_in_m_s, minutes, seconds)
            else -> stringResource(Res.string.ends_in_s, seconds)
        }
    }

    val remaining by produceState(initialValue = remainingSeconds()) {
        while (true) {
            value = remainingSeconds()
            // Tick every minute until last hour, then every second
            val delayMs = if (value >= 3_600) 60_000L else 1_000L
            delay(delayMs)
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painter = painterResource(Res.drawable.clock), contentDescription = null)
        Text(
            text = format(remaining),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun SubmissionActions(
    viewState: SubmissionViewState,
    onSubmitWork: () -> Unit,
    onRetry: () -> Unit,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = when (viewState) {
            is SubmissionViewState.ImageTakenLocally -> {
                when (viewState.uploadStatus) {
                    is SubmissionUploadState.Uploading -> viewState.uploadStatus.progress ?: 0f
                    SubmissionUploadState.Success -> 1f
                    else -> 0f
                }
            }

            else -> 0f
        }
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            8.dp,
            Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (viewState) {
            is SubmissionViewState.ImageTakenLocally -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    when (viewState.uploadStatus) {
                        is SubmissionUploadState.Error -> FilledIconButton(
                            onClick = onRetry,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Try,
                                contentDescription = null,
                            )
                        }

                        SubmissionUploadState.Success -> CircularProgressIndicator()
                        is SubmissionUploadState.Uploading -> CircularProgressIndicator(
                            progress = {
                                animatedProgress
                            },
                        )
                    }
                    Text(
                        text = when (viewState.uploadStatus) {
                            is SubmissionUploadState.Error -> stringResource(Res.string.upload_failed)
                            SubmissionUploadState.Success -> stringResource(Res.string.upload_complete)
                            is SubmissionUploadState.Uploading -> stringResource(Res.string.uploading_your_image)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
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

            is SubmissionViewState.Submitted -> {}
        }
    }
}

@Composable
private fun SubmissionDeleteConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(Res.string.delete_entry))
        },
        text = {
            Text(text = stringResource(Res.string.are_you_sure_delete_entry))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(Res.string.yes_delete_it))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(Res.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageSourcePickerSheet(
    viewState: JamViewState,
    onCameraSelected: () -> Unit,
    onGallerySelected: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(viewState.dialog) {
        if (viewState.dialog == JamScreenDialog.ImageSourceSheet) {
            sheetState.show()
        }
    }

    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible) {
            onDismissRequest()
        }
    }

    if (viewState.dialog == JamScreenDialog.ImageSourceSheet) {
        ModalBottomSheet(
            onDismissRequest = {},
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Column {
                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.take_a_picture))
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable(
                        onClick = onCameraSelected
                    ),
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                )
                ListItem(
                    headlineContent = {
                        Text(stringResource(Res.string.pick_one_from_the_gallery))
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                    modifier = Modifier.clickable(
                        onClick = onGallerySelected
                    ),
                )
            }
        }
    }
}

