package com.scritch.app.util

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class MessageMapper : KoinComponent {

    private val launcher: Launcher by inject()

    fun mapMailUrl(message: EnquiryMessage) =
        EnquiryMessage.MAIL_URI
            .replace(EnquiryMessage.TO_PLACEHOLDER, launcher.encode(message.to))
            .replace(EnquiryMessage.SUBJECT_PLACEHOLDER, launcher.encode(message.subject))
            .replace(EnquiryMessage.BODY_PLACEHOLDER, launcher.encode(message.message))
}