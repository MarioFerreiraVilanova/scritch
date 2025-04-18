package com.scritch.app.home

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration
import be.digitalia.compose.htmlconverter.HtmlStyle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString

@Composable
actual fun HtmlText (tip: Map.Entry<String, String>) {
    val uriHandler = LocalUriHandler.current
    val linkColor = MaterialTheme.colorScheme.primary
    val annotatedString = remember(tip.value) {
        htmlToAnnotatedString(
            html = tip.value,
            style = HtmlStyle(
                textLinkStyles = TextLinkStyles(
                    SpanStyle(
                        textDecoration = TextDecoration.Underline,
                        color = linkColor,
                    )
                )
            ),
            linkInteractionListener = { annotation ->
                if (annotation is LinkAnnotation.Url) {
                    uriHandler.openUri(annotation.url)
                }
            }
        )
    }
    Text(
        text = annotatedString,
    )
}