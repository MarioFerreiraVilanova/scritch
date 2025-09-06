package com.scritch.app.jam.ui

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.domain.config.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.domain.models.PhotoResult
import io.github.ismoy.imagepickerkmp.presentation.ui.components.ImagePickerLauncher

@Composable
fun FullScreenCamera(
    onPhotoCaptured: (PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit
) {
    ImagePickerLauncher(
        config = ImagePickerConfig(
            onPhotoCaptured = onPhotoCaptured,
            onError = onError,
            onDismiss = onDismiss,
            directCameraLaunch = true,
        )
    )
}