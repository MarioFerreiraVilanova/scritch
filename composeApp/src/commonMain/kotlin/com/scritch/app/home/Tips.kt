package com.scritch.app.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import be.digitalia.compose.htmlconverter.HtmlStyle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString

@Composable
fun Tips(
    title: String,
    tips: Map<String, String>,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            items = tips.entries.toList(),
        ) { tip ->
            TipsText(
                tip = tip,
            )
        }
    }
}

@Composable
private fun TipsText(
    tip: Map.Entry<String, String>,
) {
    Column {
        Text(
            text = tip.key,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        if (tip.value.firstOrNull() == '<') {
            HtmlText(tip = tip)
        } else {
            Text(
                text = tip.value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
expect fun HtmlText (tip: Map.Entry<String, String>)