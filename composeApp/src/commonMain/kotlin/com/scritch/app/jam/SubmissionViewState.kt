package com.scritch.app.jam

import io.github.ismoy.imagepickerkmp.CameraPhotoHandler

sealed class SubmissionViewState {
    object NotSubmitted : SubmissionViewState()
    data class ImageTaken(
        val image: CameraPhotoHandler.PhotoResult,
        val uploadStatus: SubmissionUploadState
    ) : SubmissionViewState()
}

sealed class SubmissionUploadState {
    data class Uploading(
        val progress: Float?
    ) : SubmissionUploadState()
    data class Error(val error: Throwable) : SubmissionUploadState()
    object Success : SubmissionUploadState()
}