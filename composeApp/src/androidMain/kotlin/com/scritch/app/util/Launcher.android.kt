package com.scritch.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue
import androidx.core.net.toUri

actual class Launcher : KoinComponent {

    val context: Context by inject()
    private val messageMapper: MessageMapper by inject()

    actual fun encode(text: String): String = Uri.encode(text)

    actual fun mail(message: EnquiryMessage) {
        kotlin.runCatching {
            val mailToUri = messageMapper.mapMailUrl(message)
            val data = mailToUri.toUri()
            Intent(Intent.ACTION_VIEW)
                .setData(data)
                .apply { setFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                .apply { context.startActivity(this) }
        }.onFailure { showErrorToast("E-mail") }
    }

    private fun showErrorToast(type: String) {
        Toast.makeText(context, "Couldn't launch $type", Toast.LENGTH_LONG).show()
    }
}