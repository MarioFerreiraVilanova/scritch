package com.scritch.app.util

expect class Launcher {
    fun encode(text: String): String
    fun mail(message: EnquiryMessage)
}