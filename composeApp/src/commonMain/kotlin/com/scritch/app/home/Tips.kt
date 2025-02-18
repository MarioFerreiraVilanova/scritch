package com.scritch.app.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun Tips(
    title: String,
    description: String,
) {
    Column(
        modifier = Modifier.navigationBarsPadding()
    ) {
        TopAppBar(
            title = {
                Text("Prompt tips")
            }
        )
        if (description.firstOrNull() == '<'){
            Text(
                text = AnnotatedString.fromHtml(
                    description,
                    linkStyles = TextLinkStyles(
                        style = SpanStyle(
                            textDecoration = TextDecoration.Underline,
                        )
                    )
                ),
                modifier = Modifier.padding(all = 16.dp)
            )
        } else {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(all = 16.dp)
            )
        }
    }
}