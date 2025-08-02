package com.scritch.app.solomode

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration

/**
 * Once AnnotatedString.fromHtml supports iOS, remove the actual and move everything to Expect
 */
@Composable
actual fun HtmlText (tip: Map.Entry<String, String>) {
    Text(
        text = AnnotatedString.fromHtml(
            htmlString = tip.value,
            linkStyles = TextLinkStyles(
                style = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        )
    )
}