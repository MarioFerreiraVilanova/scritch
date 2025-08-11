package com.scritch.app.util

import androidx.core.net.toUri
import dev.gitlive.firebase.storage.File

actual fun storageFileFromString(pathOrUri: String): File {
    return File(pathOrUri.toUri())
}