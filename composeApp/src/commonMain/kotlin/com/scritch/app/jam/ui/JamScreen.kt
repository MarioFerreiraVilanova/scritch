package com.scritch.app.jam.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
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
import com.scritch.app.jam.JamFeedState
import com.scritch.app.jam.JamScreenDialog
import com.scritch.app.jam.JamSubmission
import com.scritch.app.jam.JamViewModel
import com.scritch.app.jam.JamViewState
import com.scritch.app.jam.LoadingState
import com.scritch.app.jam.ModerationStatus
import com.scritch.app.jam.SubmissionUploadState
import com.scritch.app.jam.SubmissionViewState
import com.scritch.app.prompt.Prompt
import com.scritch.app.prompt.TipsSheet
import com.scritch.app.uicomponents.Button
import com.scritch.app.uicomponents.PageLoader
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
import scritch.composeapp.generated.resources.ends_in_d_h
import scritch.composeapp.generated.resources.ends_in_h_m
import scritch.composeapp.generated.resources.ends_in_m_s
import scritch.composeapp.generated.resources.ends_in_s
import scritch.composeapp.generated.resources.entry_approved
import scritch.composeapp.generated.resources.entry_approved_message
import scritch.composeapp.generated.resources.entry_rejected
import scritch.composeapp.generated.resources.entry_rejected_message
import scritch.composeapp.generated.resources.ok
import scritch.composeapp.generated.resources.pick_one_from_the_gallery
import scritch.composeapp.generated.resources.rejected
import scritch.composeapp.generated.resources.review_pending
import scritch.composeapp.generated.resources.review_pending_message
import scritch.composeapp.generated.resources.share_your_work
import scritch.composeapp.generated.resources.status_approved
import scritch.composeapp.generated.resources.status_pending
import scritch.composeapp.generated.resources.submission_preview
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
    onImagePathReceived: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: JamViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()

    LaunchedEffect(chosenImagePath) {
        viewModel.onImageCaptured(chosenImagePath ?: return@LaunchedEffect)
        onImagePathReceived()
    }

    Scaffold(
        modifier = modifier,
    ) { contentPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .consumeWindowInsets(contentPadding)
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues.Absolute(
                left = 16.dp,
                right = 16.dp,
                top = 16.dp,
                bottom = 16.dp,
            )
        ) {
            // Description / header
            item {
                JamHeader(
                    endDate = viewState.endDate,
                )
            }

            when (viewState.loadingState) {
                LoadingState.INITIAL_LOADING -> {
                    item {
                        PageLoader(
                            modifier = Modifier.padding(64.dp)
                        )
                    }
                }

                LoadingState.NO_JAM -> {
                    item {
                        NoJamView()
                    }
                }

                else -> {
                    // Prompt
                    item {
                        Prompt(
                            viewState = viewState.promptViewState,
                            onCategoryClick = viewModel::onCategoryClick,
                        )
                    }
                    // User section
                    item {
                        UserSection(
                            submissionState = viewState.submissionState,
                            onSubmitWork = viewModel::onSubmitWork,
                            onRemoveSubmission = viewModel::onRemoveSubmission,
                            onRetryUpload = viewModel::onRetryUpload,
                            onModerationStatusClick = viewModel::onModerationStatusClick,
                        )
                    }
                    // Inspiration

                    // Other user's entries
                    jamFeed(
                        feedState = viewState.feedState,
                        onLoadMore = viewModel::onLoadMore,
                    )
                }
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
        JamScreenDialog.EntryPreview -> {
            (viewState.submissionState as? SubmissionViewState.Submitted)?.let { submission ->
                SubmissionPreviewDialog(
                    viewState = submission,
                    onDismissRequest = {
                        viewModel.onDismissDialog(JamScreenDialog.EntryPreview)
                    },
                )
            }
        }

        JamScreenDialog.SubmissionDeleteConfirmation -> SubmissionDeleteConfirmationDialog(
            onDismissRequest = {
                viewModel.onDismissDialog(JamScreenDialog.SubmissionDeleteConfirmation)
            },
            onConfirm = viewModel::onRemoveSubmission,
        )

        JamScreenDialog.ImageSourceSheet,
        null -> {
        }

        JamScreenDialog.ModerationStatus -> {
            (viewState.submissionState as? SubmissionViewState.Submitted)?.let { submission ->
                ModerationStatusDialog(
                    moderationStatus = submission.moderationStatus,
                    onDismissRequest = {
                        viewModel.onDismissDialog(JamScreenDialog.ModerationStatus)
                    }
                )
            }
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

private fun LazyListScope.jamFeed(
    feedState: JamFeedState,
    onLoadMore: () -> Unit,
){
    if (feedState.items.isNotEmpty()) {
        item {
            HorizontalDivider()
        }
        item {
            Text(
                text = "See what others are drawing",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        items(
            items = feedState.items.chunked(2),
            key = { row -> row.joinToString { it.userId } }
        ) { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SubmissionCell(
                    submission = row[0],
                    modifier = Modifier.weight(1f)
                )
                if (row.size == 2) {
                    SubmissionCell(
                        submission = row[1],
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }
            }
        }

        if (!feedState.endReached) {
            item {
                LaunchedEffect(
                    key1 = feedState.items.size
                ) {
                    onLoadMore()
                } // or use a VisibilityObserver
            }
        }
    }
}

@Composable
private fun SubmissionCell(
    submission: JamSubmission,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small)
            //.clickable { onClick(submission) }
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        KamelImage(
            resource = { asyncPainterResource(submission.imageUrl) },
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun JamHeader(
    endDate: LocalDateTime?,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.weekly_jam_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        AnimatedVisibility(endDate != null) {
            JamEndCountdown(
                endDate = endDate ?: return@AnimatedVisibility
            )
        }
        HorizontalDivider()
    }
}

@Composable
private fun NoJamView() {
    Text(
        text = stringResource(Res.string.weekly_jam_not_available),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun UserSection(
    submissionState: SubmissionViewState,
    onSubmitWork: () -> Unit,
    onRemoveSubmission: () -> Unit,
    onRetryUpload: () -> Unit,
    onModerationStatusClick: () -> Unit,
) {
    Column {
        val submitted = submissionState as? SubmissionViewState.Submitted
        AnimatedVisibility(
            visible = submitted != null,
        ) {
            EntryPreview(
                imageUrl = submitted?.imageUrl ?: return@AnimatedVisibility,
                onRetakeImage = onSubmitWork,
                onDeleteImage = onRemoveSubmission,
                moderationStatus = submissionState.moderationStatus,
                onModerationStatusClick = onModerationStatusClick,
            )
        }
        SubmissionActions(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            viewState = submissionState,
            onSubmitWork = onSubmitWork,
            onRetry = onRetryUpload,
        )
    }
}

@Composable
private fun EntryPreview(
    imageUrl: String,
    moderationStatus: ModerationStatus,
    onRetakeImage: () -> Unit,
    onDeleteImage: () -> Unit,
    onModerationStatusClick: () -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clip(MaterialTheme.shapes.medium),
        contentAlignment = Alignment.BottomEnd,
    ) {

        // 2) Real image (fades/scales in over the skeleton)
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
                        progress = {
                            progress
                        },
                    )
                }
            },
            modifier = Modifier.aspectRatio(1f)
        )

        // 3) Action buttons only after image is visible
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ){
            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable(onClick = onModerationStatusClick),
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
                FilledIconButton(onClick = onRetakeImage) {
                    Icon(imageVector = Icons.Default.Repeat, contentDescription = null)
                }
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
    modifier: Modifier = Modifier,
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
        modifier = modifier,
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
                                imageVector = Icons.Default.Error,
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
private fun SubmissionPreviewDialog(
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
                        contentScale = ContentScale.FillWidth,        // don’t fill; keep aspect
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

@Composable
private fun ModerationStatusDialog(
    moderationStatus: ModerationStatus,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = when (moderationStatus) {
                    ModerationStatus.Pending -> stringResource(Res.string.review_pending)
                    ModerationStatus.Approved -> stringResource(Res.string.entry_approved)
                    ModerationStatus.Rejected -> stringResource(Res.string.entry_rejected)
                }
            )
        },
        text = {
            Text(
                text = when (moderationStatus) {
                    ModerationStatus.Pending -> stringResource(Res.string.review_pending_message)
                    ModerationStatus.Approved -> stringResource(Res.string.entry_approved_message)
                    ModerationStatus.Rejected -> stringResource(Res.string.entry_rejected_message)
                }
            )
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(Res.string.ok))
            }
        },
        icon = {
            Icon(
                imageVector = when (moderationStatus) {
                    ModerationStatus.Pending -> Icons.Default.Info
                    ModerationStatus.Approved -> Icons.Default.CheckCircle
                    ModerationStatus.Rejected -> Icons.Default.Error
                },
                contentDescription = null,
                tint = when (moderationStatus) {
                    ModerationStatus.Pending -> MaterialTheme.colorScheme.primary
                    ModerationStatus.Approved -> MaterialTheme.colorScheme.primary
                    ModerationStatus.Rejected -> MaterialTheme.colorScheme.error
                }
            )
        }
    )
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
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
            ) {
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

