package com.scritch.app.util

expect suspend fun readBytesFromUriString(uriString: String): ByteArray
expect suspend fun uploadWithOptionalProgress(
    storagePath: String,
    bytes: ByteArray,
    mimeType: String = "image/jpeg",
    onProgress: ((Int) -> Unit)? = null
): String // returns download URL