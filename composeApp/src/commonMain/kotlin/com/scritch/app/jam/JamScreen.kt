package com.scritch.app.jam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scritch.app.prompt.Prompt
import com.scritch.app.prompt.TipsSheet
import com.scritch.app.util.dayOfWeekString
import io.github.ismoy.imagepickerkmp.GalleryPhotoHandler
import io.github.ismoy.imagepickerkmp.GalleryPickerLauncher
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.clock
import scritch.composeapp.generated.resources.weekly_jam_description
import scritch.composeapp.generated.resources.weekly_jam_end_time
import scritch.composeapp.generated.resources.weekly_jam_not_available

@Composable
fun JamScreen(
    modifier: Modifier = Modifier,
    viewModel: JamViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()
    var showGallery by remember { mutableStateOf(false) }
    var selectedImages by remember { mutableStateOf<List<GalleryPhotoHandler.PhotoResult>>(emptyList()) }

    Scaffold(
        modifier = modifier,
    ) { contentPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .consumeWindowInsets(contentPadding)
                .padding(contentPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    text = stringResource(Res.string.weekly_jam_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            viewState.endDate?.let { endDate ->
                item {
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
            }
            item {
                HorizontalDivider()
            }
            item {
                when (viewState.loadingState) {
                    LoadingState.LOADING -> {
                        CircularProgressIndicator()
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
            }
            item {
                Button(
                    onClick = {
                        showGallery = true
                    }
                ) {
                    Text("Submit entry")
                }
            }
        }
    }

    if (showGallery) {
        GalleryPickerLauncher(
            onPhotosSelected = { photos ->
                selectedImages = photos
                showGallery = false
            },
            onError = { error ->
                showGallery = false
            },
            onDismiss = {
                println("User cancelled or dismissed the picker")
                showGallery = false // Reset state when user doesn't select anything
            },
            allowMultiple = true, // False for single selection
            mimeTypes = listOf("image/jpeg", "image/png") // Optional: filter by type
        )
    }

    TipsSheet(
        viewState = viewState.promptViewState,
        onTipDisplayed = viewModel::onTipDisplayed,
    )
}
