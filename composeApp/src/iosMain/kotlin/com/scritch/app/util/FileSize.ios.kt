package com.scritch.app.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.*

/**
 * iOS implementation for file size checking.
 */
actual suspend fun getFileSizeInBytes(pathOrUri: String): Long? = withContext(Dispatchers.Default) {
    try {
        val nsUrl = if (pathOrUri.startsWith("file://")) {
            NSURL.URLWithString(pathOrUri)
        } else {
            NSURL.fileURLWithPath(pathOrUri)
        }
        
        if (nsUrl == null) {
            println("FileSize.ios: Invalid URL: $pathOrUri")
            return@withContext null
        }
        
        val fileManager = NSFileManager.defaultManager
        val attributes = fileManager.attributesOfItemAtPath(nsUrl.path!!, error = null)
            ?: return@withContext null
        
        val fileSize = attributes[NSFileSize] as? NSNumber
        fileSize?.longValue
    } catch (e: Exception) {
        println("FileSize.ios: Error getting file size: ${e.message}")
        null
    }
}