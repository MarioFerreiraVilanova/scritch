package com.scritch.app.util

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class EmailClient {
    fun open(
        to: String,
        onComplete: (Boolean) -> Unit,
    )
}