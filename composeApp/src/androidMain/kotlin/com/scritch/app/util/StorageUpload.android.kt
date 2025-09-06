package com.scritch.app.util

import dev.gitlive.firebase.storage.File
import dev.gitlive.firebase.storage.FirebaseStorageMetadata
import dev.gitlive.firebase.storage.StorageReference

/**
 * Android implementation using resumable uploads with progress tracking.
 * This provides the best user experience with progress bars and network resilience.
 */
actual suspend fun uploadFileToStorage(
    ref: StorageReference,
    file: File,
    meta: FirebaseStorageMetadata,
    onProgress: ((Int) -> Unit)?
) {
    println("StorageUpload.android: Starting resumable upload...")
    
    // Use resumable upload with progress updates for Android
    ref.putFileResumable(file, meta).collect { prog ->
        val transferred = prog.bytesTransferred.toLong()
        val total = prog.totalByteCount.toLong()
        val pct = if (total > 0L) ((transferred * 100L) / total).toInt() else 0
        println("StorageUpload.android: Upload progress: $pct% ($transferred/$total bytes)")
        onProgress?.invoke(pct.coerceIn(0, 99)) // we'll send 100 after success
    }
    
    println("StorageUpload.android: Upload completed successfully")
}