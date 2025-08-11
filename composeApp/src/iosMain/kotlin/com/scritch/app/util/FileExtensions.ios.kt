package com.scritch.app.util

import platform.Foundation.NSURL
import dev.gitlive.firebase.storage.File

actual fun storageFileFromString(pathOrUri: String): File {
    return File(NSURL.fileURLWithPath(pathOrUri))
}