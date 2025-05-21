package com.scritch.app.util

import platform.UIKit.UIApplication
import platform.Foundation.NSURL

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class EmailClient {
    actual fun open(
        to: String,
        onComplete: (Boolean) -> Unit,
    ){
        val emailUrl = "mailto:$to"
        val url = NSURL(string = emailUrl)
        UIApplication.sharedApplication.openURL(
            url = url,
            options = emptyMap<Any?, Any?>(),
            completionHandler = { completed: Boolean ->
                onComplete(completed)
            }
        )
    }
}