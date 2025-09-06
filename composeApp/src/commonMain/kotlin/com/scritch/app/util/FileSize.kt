package com.scritch.app.util

/**
 * Platform-specific file size utilities.
 */
expect suspend fun getFileSizeInBytes(pathOrUri: String): Long?

/**
 * Maximum allowed file size in bytes (8MB)
 */
const val MAX_FILE_SIZE_BYTES = 8L * 1024L * 1024L

/**
 * Validates if a file is within the allowed size limit.
 * @param pathOrUri The file path or URI
 * @return true if the file is within limits, false if too large, null if size cannot be determined
 */
suspend fun isFileSizeValid(pathOrUri: String): Boolean? {
    val fileSizeInBytes = getFileSizeInBytes(pathOrUri) ?: return null
    return fileSizeInBytes <= MAX_FILE_SIZE_BYTES
}