package com.scritch.app.jam.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.scritch.app.jam.JamFeedState
import com.scritch.app.jam.JamScreenDialog
import com.scritch.app.jam.JamSubmission
import com.scritch.app.jam.JamViewModel
import com.scritch.app.jam.JamViewState
import com.scritch.app.jam.LoadingState
import com.scritch.app.jam.SubmissionViewState
import com.scritch.app.jam.ui.components.EmptyFeedCell
import com.scritch.app.jam.ui.components.JamHeader
import com.scritch.app.jam.ui.components.SubmissionCell
import com.scritch.app.jam.ui.components.UserSubmissionCell
import com.scritch.app.jam.ui.components.dialogs.FileSizeExceededDialog
import com.scritch.app.jam.ui.components.dialogs.ModerationStatusDialog
import com.scritch.app.jam.ui.components.dialogs.SubmissionDeleteDialog
import com.scritch.app.jam.ui.components.dialogs.SubmissionPreviewDialog
import com.scritch.app.prompt.Prompt
import com.scritch.app.prompt.TipsSheet
import com.scritch.app.uicomponents.PageLoader
import io.github.ismoy.imagepickerkmp.presentation.ui.components.GalleryPickerLauncher
import kotlinx.coroutines.delay
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.camera
import scritch.composeapp.generated.resources.image
import scritch.composeapp.generated.resources.jam_had_no_entries
import scritch.composeapp.generated.resources.no_jam_subtitle
import scritch.composeapp.generated.resources.no_jam_title
import scritch.composeapp.generated.resources.pick_one_from_the_gallery
import scritch.composeapp.generated.resources.show_contributions
import scritch.composeapp.generated.resources.show_contributions_subtitle
import scritch.composeapp.generated.resources.take_a_picture
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class, kotlin.time.ExperimentalTime::class)
@Composable
fun JamScreen(
    chosenImagePath: String?,
    onOpenCamera: () -> Unit,
    onImagePathReceived: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: JamViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()

    // Calculate jam expiration with smart adaptive ticking
    var isJamExpired by remember { mutableStateOf(false) }

    // Smart ticking: adjust frequency based on time remaining
    LaunchedEffect(viewState.endDate) {
        if (viewState.endDate != null) {
            viewState.endDate?.toInstant(TimeZone.currentSystemDefault())?.let { endInstant ->
                while (true) {
                    val now = Clock.System.now()
                    val timeLeft = endInstant - now

                    // Update expiration state
                    isJamExpired = now > endInstant

                    val delayMs = when {
                        timeLeft <= 0.minutes -> break // Jam has ended, stop ticking
                        timeLeft <= 10.minutes -> 1_000L // Tick every second in final 10 minutes
                        timeLeft <= 1.hours -> 60_000L // Tick every minute in final hour
                        else -> 600_000L // Tick every 10 minutes otherwise
                    }

                    delay(delayMs)
                }
            }
        }
    }
    val pullToRefreshState = rememberPullToRefreshState()
    val isRefreshing = viewState.loadingState == LoadingState.REFRESHING

    LaunchedEffect(chosenImagePath) {
        viewModel.onImageCaptured(chosenImagePath ?: return@LaunchedEffect)
        onImagePathReceived()
    }

    Scaffold(
        modifier = modifier,
    ) { contentPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::onRefresh,
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(contentPadding)
                .padding(contentPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
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

                        // Show contributions toggle (only if there are contributions to show)
                        if (viewState.feedState.items.isNotEmpty()) {
                            // Separator before toggle
                            item {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            item {
                                ShowContributionsToggle(
                                    showContributions = viewState.showContributions,
                                    onToggleChange = viewModel::onToggleContributions
                                )
                            }
                        }

                        // Feed with user submission integrated
                        jamFeed(
                            feedState = viewState.feedState,
                            submissionState = viewState.submissionState,
                            onSubmitWork = viewModel::onSubmitWork,
                            onModerationStatusClick = viewModel::onModerationStatusClick,
                            onShowUserPreview = viewModel::onShowUserPreview,
                            onShowSubmissionPreview = viewModel::onShowSubmissionPreview,
                            showContributions = viewState.showContributions,
                            isJamExpired = isJamExpired,
                            onLoadMore = viewModel::onLoadMore,
                        )
                    }
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
        onDismissRequest = viewModel::onDismissDialog,
    )

    when (val dialog = viewState.dialog) {
        is JamScreenDialog.EntryPreview -> {
            SubmissionPreviewDialog(
                imageUrl = dialog.imageUrl,
                isUserSubmission = dialog.isUserSubmission,
                moderationStatus = dialog.moderationStatus,
                nickname = dialog.nickname,
                isJamExpired = isJamExpired,
                onDismissRequest = viewModel::onDismissDialog,
                onRetrySubmission = if (dialog.isUserSubmission) viewModel::onSubmitWork else null,
                onDeleteSubmission = if (dialog.isUserSubmission) viewModel::onRemoveSubmission else null,
                onModerationStatusClick = if (dialog.isUserSubmission) viewModel::onModerationStatusClick else null,
            )
        }

        JamScreenDialog.SubmissionDeleteConfirmation -> SubmissionDeleteDialog(
            onDismissRequest = viewModel::onDismissDialog,
            onConfirm = viewModel::onRemoveSubmission,
        )

        JamScreenDialog.ImageSourceSheet,
        null -> {
        }

        JamScreenDialog.ModerationStatusExplanation -> {
            (viewState.submissionState as? SubmissionViewState.Submitted)?.let { submission ->
                ModerationStatusDialog(
                    moderationStatus = submission.moderationStatus,
                    onDismissRequest = viewModel::onDismissDialog,
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
                onDismiss = viewModel::onDismissDialog,
                onError = { _ ->
                    // TODO handle the error, maybe show a snack bar or something
                    viewModel.onDismissDialog()
                },
            )
        }

        JamScreenDialog.FileSizeExceeded -> {
            FileSizeExceededDialog(
                onDismissRequest = viewModel::onDismissDialog,
            )
        }
    }
}

private fun LazyListScope.jamFeed(
    feedState: JamFeedState,
    submissionState: SubmissionViewState,
    onSubmitWork: () -> Unit,
    onModerationStatusClick: () -> Unit,
    onShowUserPreview: () -> Unit,
    onShowSubmissionPreview: (String) -> Unit,
    showContributions: Boolean,
    isJamExpired: Boolean,
    onLoadMore: () -> Unit,
) {
    // Add separator before feed if there are no contributions (so no toggle separator was shown)
    if (feedState.items.isEmpty()) {
        item {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline
            )
        }
    }

    // Create combined list with user submission as first item
    val allSubmissions = feedState.items
    val hasUserSubmission = submissionState is SubmissionViewState.Submitted
    val hasAnyContent = hasUserSubmission || allSubmissions.isNotEmpty()

    if (hasAnyContent) {

        // Create rows, starting with user submission if present
        val allRows = mutableListOf<List<Any>>()

        if (!isJamExpired || hasUserSubmission) {
            // First row always includes user submission (whether submitted or empty state)
            if (allSubmissions.isNotEmpty()) {
                allRows.add(listOf("user", allSubmissions[0]))
                // Add remaining submissions in pairs
                allSubmissions.drop(1).chunked(2).forEach { row ->
                    allRows.add(row)
                }
            } else {
                allRows.add(listOf("user"))
            }
        } else {
            // No user submission, just show other submissions
            allRows.addAll(allSubmissions.chunked(2))
        }

        items(
            items = allRows,
            key = { row ->
                row.joinToString { item ->
                    when (item) {
                        "user" -> "user_submission"
                        is JamSubmission -> item.userId
                        else -> item.toString()
                    }
                }
            }
        ) { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEachIndexed { index, item ->
                    when (item) {
                        "user" -> {
                            UserSubmissionCell(
                                submissionState = submissionState,
                                onSubmitWork = onSubmitWork,
                                onModerationStatusClick = onModerationStatusClick,
                                onShowPreview = onShowUserPreview,
                                isJamExpired = isJamExpired,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        is JamSubmission -> {
                            SubmissionCell(
                                submission = item,
                                showImage = showContributions,
                                onShowPreview = onShowSubmissionPreview,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Fill remaining space if row is incomplete
                if (row.size == 1) {
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
                }
            }
        }
    } else {
        // Empty state - show user submission slot and empty feed cell only if jam is active or user has submission
        if (!isJamExpired || hasUserSubmission) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UserSubmissionCell(
                        submissionState = submissionState,
                        onSubmitWork = onSubmitWork,
                        onModerationStatusClick = onModerationStatusClick,
                        onShowPreview = onShowUserPreview,
                        isJamExpired = isJamExpired,
                        modifier = Modifier.weight(1f)
                    )
                    EmptyFeedCell(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            // Expired jam with no contributions
            item {
                Text(
                    text = stringResource(Res.string.jam_had_no_entries),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(64.dp)
                )
            }
        }
    }
}


@Composable
private fun ShowContributionsToggle(
    showContributions: Boolean,
    onToggleChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(Res.string.show_contributions),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(Res.string.show_contributions_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = showContributions,
            onCheckedChange = onToggleChange
        )
    }
}

@Composable
private fun NoJamView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(Res.string.no_jam_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(Res.string.no_jam_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
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
                            painter = painterResource(Res.drawable.camera),
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
                            painter = painterResource(Res.drawable.image),
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