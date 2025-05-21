package com.scritch.app.util

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

actual class Launcher: KoinComponent {

    private val emailLauncher: EmailLauncher by inject()

    actual fun encode(text: String): String {
        return text.map { char ->
            when (char) {
                in 'A'..'Z', in 'a'..'z', in '0'..'9', '-', '_', '.', '~' -> char.toString()
                else -> "%${char.code.toString(16).uppercase()}"
            }
        }.joinToString("")
    }

    actual fun mail(message: EnquiryMessage) {
        emailLauncher.composeEmail(message.to, message.subject, message.message)
    }
}