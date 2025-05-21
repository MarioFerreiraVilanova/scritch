package com.scritch.app.settings.about

import androidx.lifecycle.ViewModel
import com.scritch.app.util.EmailClient

class AboutViewModel(
    val emailClient: EmailClient,
): ViewModel() {

    fun onSendFeedback(){
        emailClient.open(
            to = "scritch@gmail.com"
        ) { success ->
            print("Email client launch result: $success")
        }
    }
}