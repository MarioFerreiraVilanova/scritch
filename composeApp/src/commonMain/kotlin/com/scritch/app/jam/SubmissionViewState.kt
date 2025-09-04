package com.scritch.app.jam

import androidx.core.uri.Uri

sealed class SubmissionViewState {
    object NotSubmitted : SubmissionViewState()
    data class ImageTakenLocally(
        val imageUri: Uri,
        val uploadStatus: SubmissionUploadState
    ) : SubmissionViewState()

    data class Submitted(
        val imageUrl: String,
        val moderationStatus: ModerationStatus,
    ) : SubmissionViewState()
}

sealed class SubmissionUploadState {
    data class Uploading(
        val progress: Float?
    ) : SubmissionUploadState()

    data class Error(val error: Throwable) : SubmissionUploadState()
    object Success : SubmissionUploadState()
}