package com.scritch.app.util

actual suspend fun readBytesFromUriString(uriString: String): ByteArray {

}

actual suspend fun uploadWithOptionalProgress(
    storagePath: String,
    bytes: ByteArray,
    mimeType: String,
    onProgress: ((Int) -> Unit)?,
): String {

}