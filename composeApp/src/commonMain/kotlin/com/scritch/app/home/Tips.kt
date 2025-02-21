package com.scritch.app.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
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
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(all = 16.dp)
            )
        }
    }
}