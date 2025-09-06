package com.scritch.app.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.context.GlobalContext
import java.io.File
import java.io.FileNotFoundException

/**
 * Android implementation for file size checking.
 */
actual suspend fun getFileSizeInBytes(pathOrUri: String): Long? = withContext(Dispatchers.IO) {
    try {
        val context = GlobalContext.get().get<Context>()
        
        when {
            pathOrUri.startsWith("content://") -> {
                // Content URI from gallery picker
                val uri = Uri.parse(pathOrUri)
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.available().toLong()
                }
            }
            pathOrUri.startsWith("file://") -> {
                // File URI
                val file = File(Uri.parse(pathOrUri).path ?: return@withContext null)
                if (file.exists()) file.length() else null
            }
            else -> {
                // Direct file path
                val file = File(pathOrUri)
                if (file.exists()) file.length() else null
            }
        }
    } catch (e: Exception) {
        println("FileSize.android: Error getting file size: ${e.message}")
        null
    }
}