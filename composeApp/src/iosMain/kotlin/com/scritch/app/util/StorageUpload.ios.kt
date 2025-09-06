package com.scritch.app.util

import dev.gitlive.firebase.storage.File
import dev.gitlive.firebase.storage.FirebaseStorageMetadata
import dev.gitlive.firebase.storage.StorageReference

/**
 * iOS implementation using simple uploads for better reliability.
 * The resumable upload has callback issues on iOS, so we use a simpler approach.
 */
actual suspend fun uploadFileToStorage(
    ref: StorageReference,
    file: File,
    meta: FirebaseStorageMetadata,
    onProgress: ((Int) -> Unit)?
) {
    println("StorageUpload.ios: Starting simple upload...")
    
    try {
        // Show some progress to user
        onProgress?.invoke(10)
        
        // Use simple putFile for iOS - more reliable than putFileResumable
        ref.putFile(file, meta)
        
        println("StorageUpload.ios: Upload completed successfully")
        onProgress?.invoke(99) // Show near completion, we'll send 100 after download URL
        
    } catch (e: Exception) {
        println("StorageUpload.ios: Upload failed: ${e.message}")
        e.printStackTrace()
        throw e
    }
}