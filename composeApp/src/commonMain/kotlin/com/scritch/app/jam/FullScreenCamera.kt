package com.scritch.app.jam

import androidx.compose.runtime.Composable
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler
import io.github.ismoy.imagepickerkmp.ImagePickerConfig
import io.github.ismoy.imagepickerkmp.ImagePickerLauncher

@Composable
fun FullScreenCamera(
    onPhotoCaptured: (CameraPhotoHandler.PhotoResult) -> Unit,
    onError: (Exception) -> Unit,
    onDismiss: () -> Unit
) {
    ImagePickerLauncher(
        config = ImagePickerConfig(
            onPhotoCaptured = onPhotoCaptured,
            onError = onError,
            onDismiss = onDismiss,
        )
    )
}