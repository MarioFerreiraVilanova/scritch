package com.scritch.app.util

import android.app.Application
import android.content.Intent
import androidx.core.net.toUri

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class EmailClient(
    val context: Application,
) {
    actual fun open(
        to: String,
        onComplete: (Boolean) -> Unit,
    ) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:$to".toUri()
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                onComplete(false)
            }
        } catch (e: Exception) {
            onComplete(false)
        }
    }
}