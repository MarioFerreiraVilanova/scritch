package com.scritch.app.util

actual suspend fun readBytesFromUriString(uriString: String): ByteArray {
    // TODO: Implement iOS version
    throw NotImplementedError("iOS implementation not yet available")
}

actual suspend fun uploadWithOptionalProgress(
    storagePath: String,
    bytes: ByteArray,
    mimeType: String,
    onProgress: ((Int) -> Unit)?,
): String {
    // TODO: Implement iOS version
    throw NotImplementedError("iOS implementation not yet available")
}