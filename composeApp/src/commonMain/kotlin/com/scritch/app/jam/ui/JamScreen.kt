package com.scritch.app.jam.ui

import com.scritch.app.jam.ui.components.JamHeader
import com.scritch.app.jam.ui.components.UserSection
import com.scritch.app.jam.ui.components.dialogs.ModerationStatusDialog
import com.scritch.app.jam.ui.components.dialogs.SubmissionDeleteDialog
import com.scritch.app.jam.ui.components.dialogs.SubmissionPreviewDialog

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.scritch.app.jam.JamFeedState
import com.scritch.app.jam.JamScreenDialog
import com.scritch.app.jam.JamSubmission
import com.scritch.app.jam.JamViewModel
import com.scritch.app.jam.JamViewState
import com.scritch.app.jam.LoadingState
import com.scritch.app.jam.SubmissionViewState
import com.scritch.app.prompt.Prompt
import com.scritch.app.prompt.TipsSheet
import com.scritch.app.uicomponents.PageLoader
import io.github.ismoy.imagepickerkmp.GalleryPickerLauncher
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.pick_one_from_the_gallery
import scritch.composeapp.generated.resources.take_a_picture
import scritch.composeapp.generated.resources.weekly_jam_not_available

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JamScreen(
    chosenImagePath: String?,
    onOpenCamera: () -> Unit,
    onImagePathReceived: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: JamViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()
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
                    // User section
                    item {
                        UserSection(
                            submissionState = viewState.submissionState,
                            onSubmitWork = viewModel::onSubmitWork,
                            onRemoveSubmission = viewModel::onRemoveSubmission,
                            onRetryUpload = viewModel::onRetryUpload,
                            onCancelUpload = viewModel::onCancelUpload,
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

        JamScreenDialog.SubmissionDeleteConfirmation -> SubmissionDeleteDialog(
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
private fun NoJamView() {
    Text(
        text = stringResource(Res.string.weekly_jam_not_available),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground,
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