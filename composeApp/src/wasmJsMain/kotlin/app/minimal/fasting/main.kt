package app.minimal.fasting

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

@JsModule("@js-joda/timezone")
external object JsJodaTimeZoneModule

private val jsJodaTz = JsJodaTimeZoneModule

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(viewportContainerId = "composeApplication") {
        App()
    }
}