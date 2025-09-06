package com.scritch.app.util

import dev.gitlive.firebase.storage.File
import dev.gitlive.firebase.storage.FirebaseStorageMetadata
import dev.gitlive.firebase.storage.StorageReference

/**
 * Platform-specific Firebase Storage upload functionality.
 * 
 * Android: Uses resumable uploads with progress callbacks
 * iOS: Uses simple uploads for better reliability
 */
expect suspend fun uploadFileToStorage(
    ref: StorageReference,
    file: File,
    meta: FirebaseStorageMetadata,
    onProgress: ((Int) -> Unit)? = null
)