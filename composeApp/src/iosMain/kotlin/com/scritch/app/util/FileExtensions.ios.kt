package com.scritch.app.util

import platform.Foundation.NSURL
import dev.gitlive.firebase.storage.File

actual fun storageFileFromString(pathOrUri: String): File {
    println("FileExtensions.ios: Converting pathOrUri to File: $pathOrUri")
    
    // Handle file:// URLs properly
    val nsUrl = if (pathOrUri.startsWith("file://")) {
        NSURL.URLWithString(pathOrUri)
    } else {
        NSURL.fileURLWithPath(pathOrUri)
    }
    
    println("FileExtensions.ios: Created NSURL: $nsUrl")
    val file = File(nsUrl!!)
    println("FileExtensions.ios: Created File object")
    return file
}