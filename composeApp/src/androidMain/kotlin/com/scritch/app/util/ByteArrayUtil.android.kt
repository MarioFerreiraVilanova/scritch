package com.scritch.app.util

import android.content.ContentResolver
import android.net.Uri
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase as AndroidFirebase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

lateinit var appContentResolver: ContentResolver // init from Application

actual suspend fun readBytesFromUriString(uriString: String): ByteArray {
    val uri = Uri.parse(uriString)
    appContentResolver.openInputStream(uri).use { input ->
        requireNotNull(input) { "Cannot open $uriString" }
        return input.readBytes()
    }
}

actual suspend fun uploadWithOptionalProgress(
    storagePath: String,
    bytes: ByteArray,
    mimeType: String,
    onProgress: ((Int) -> Unit)?
): String = suspendCancellableCoroutine { cont ->
    val ref = AndroidFirebase.storage.reference.child(storagePath)
    val metadata = com.google.firebase.storage.StorageMetadata.Builder()
        .setContentType(mimeType)
        .build()

    val task = ref.putBytes(bytes, metadata)
    if (onProgress != null) {
        task.addOnProgressListener { snap ->
            val pct = if (snap.totalByteCount > 0)
                ((100.0 * snap.bytesTransferred) / snap.totalByteCount).toInt()
            else 0
            onProgress(pct.coerceIn(0, 99)) // 100 will be sent when finished
        }
    }

    task
        .addOnFailureListener { cont.resumeWithException(it) }
        .addOnSuccessListener {
            ref.downloadUrl
                .addOnFailureListener { e -> cont.resumeWithException(e) }
                .addOnSuccessListener { uri ->
                    onProgress?.invoke(100)
                    cont.resume(uri.toString())
                }
        }

    cont.invokeOnCancellation { task.cancel() }
}