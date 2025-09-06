package com.scritch.app.util

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.min
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

/**
 * Rate limiting utility for upload operations to prevent abuse.
 */
class UploadRateLimit {
    private val mutex = Mutex()
    private var lastUploadTime: TimeSource.Monotonic.ValueTimeMark? = null
    private var uploadAttempts = 0
    private var windowStartTime: TimeSource.Monotonic.ValueTimeMark? = null
    
    companion object {
        // Minimum time between uploads (starts at 30 seconds)
        private val BASE_COOLDOWN = 30.seconds
        
        // Maximum cooldown (10 minutes)
        private val MAX_COOLDOWN = 10.minutes
        
        // Time window for counting attempts (15 minutes)
        private val RATE_LIMIT_WINDOW = 15.minutes
        
        // Maximum uploads per window
        private const val MAX_UPLOADS_PER_WINDOW = 5
        
        // Exponential backoff multiplier
        private const val BACKOFF_MULTIPLIER = 2.0
    }
    
    /**
     * Checks if an upload is allowed and returns the result.
     * @return UploadPermission indicating if upload is allowed and any required delay
     */
    suspend fun checkUploadPermission(): UploadPermission = mutex.withLock {
        val now = TimeSource.Monotonic.markNow()
        
        // Reset window if expired
        if (windowStartTime?.let { it.elapsedNow() > RATE_LIMIT_WINDOW } != false) {
            windowStartTime = now
            uploadAttempts = 0
        }
        
        // Check if we've exceeded the rate limit
        if (uploadAttempts >= MAX_UPLOADS_PER_WINDOW) {
            val remainingWindow = RATE_LIMIT_WINDOW - (windowStartTime?.elapsedNow() ?: Duration.ZERO)
            return UploadPermission.RateLimited(remainingWindow)
        }
        
        // Check cooldown period
        lastUploadTime?.let { lastTime ->
            val timeSinceLastUpload = lastTime.elapsedNow()
            val requiredCooldown = calculateCooldown(uploadAttempts)
            
            if (timeSinceLastUpload < requiredCooldown) {
                val remainingCooldown = requiredCooldown - timeSinceLastUpload
                return UploadPermission.Cooldown(remainingCooldown)
            }
        }
        
        return UploadPermission.Allowed
    }
    
    /**
     * Records a successful upload attempt.
     */
    suspend fun recordUploadAttempt() = mutex.withLock {
        val now = TimeSource.Monotonic.markNow()
        lastUploadTime = now
        
        // Initialize window if needed
        if (windowStartTime == null) {
            windowStartTime = now
        }
        
        uploadAttempts++
    }
    
    /**
     * Calculates the required cooldown based on number of recent attempts.
     */
    private fun calculateCooldown(attempts: Int): Duration {
        if (attempts <= 1) return BASE_COOLDOWN
        
        val multiplier = BACKOFF_MULTIPLIER.pow((attempts - 1).toDouble())
        val cooldownSeconds = (BASE_COOLDOWN.inWholeSeconds * multiplier).toLong()
        
        return Duration.parse("${min(cooldownSeconds, MAX_COOLDOWN.inWholeSeconds)}s")
    }
}

/**
 * Represents the permission status for an upload attempt.
 */
sealed class UploadPermission {
    /**
     * Upload is allowed to proceed.
     */
    data object Allowed : UploadPermission()
    
    /**
     * Upload is blocked due to cooldown period.
     * @param remainingTime Time remaining until next upload is allowed
     */
    data class Cooldown(val remainingTime: Duration) : UploadPermission()
    
    /**
     * Upload is blocked due to rate limiting.
     * @param windowResetTime Time remaining until the rate limit window resets
     */
    data class RateLimited(val windowResetTime: Duration) : UploadPermission()
}