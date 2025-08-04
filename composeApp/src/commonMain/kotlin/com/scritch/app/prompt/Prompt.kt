package com.scritch.app.prompt

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle
import com.scritch.app.categories.OptionState

@Composable
fun Prompt(
    viewState: PromptViewState,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    promptFromViewState(
        viewState = viewState,
    )?.let { prompt ->
        ClickableText(
            text = prompt,
            style = MaterialTheme.typography.headlineMedium,
            modifier = modifier,
        ) { offset ->
            prompt.getStringAnnotations(offset, offset).firstOrNull()?.item?.let {
                onCategoryClick(it)
            }
        }
    }
}

@Composable
private fun promptFromViewState(
    viewState: PromptViewState,
): AnnotatedString? {
    if (viewState.medium == null && viewState.support == null) return null

    return buildAnnotatedString {
        if (viewState.topic?.prompt != null) {
            appendCategory(
                option = viewState.topic,
                suffix = if (viewState.support?.prompt != null || viewState.medium?.prompt != null) {
                    ", "
                } else if (viewState.constraint?.prompt != null) {
                    ". "
                } else {
                    "."
                },
            )
        }
        if (viewState.support?.prompt != null) {
            appendCategory(
                option = viewState.support,
                suffix = if (viewState.medium?.prompt != null) {
                    ", "
                } else if (viewState.constraint?.prompt != null) {
                    ". "
                } else {
                    "."
                }
            )
        }
        if (viewState.medium?.prompt != null) {
            appendCategory(
                option = viewState.medium,
                suffix = if (viewState.constraint?.prompt != null) {
                    ". "
                } else {
                    "."
                }
            )
        }
        if (viewState.constraint?.prompt != null) {
            appendCategory(
                option = viewState.constraint,
                suffix = "."
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun AnnotatedString.Builder.appendCategory(
    option: OptionState?,
    suffix: String?,
) {
    option?.prompt?.let {
        val textStyle =
            MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            ).toSpanStyle()
        val highlightedStyle = textStyle.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black
        )
        val linkStyle = highlightedStyle.copy(
            textDecoration = TextDecoration.Underline,
        )
        val startOfLink = option.prompt.indexOfFirst { it == '*' }.coerceAtLeast(0)
        val endOfLink = option.prompt.indexOfLast { it == '*' }.coerceAtLeast(0)
        withStyle(
            style = textStyle
        ) {
            append(option.prompt.substring(0, startOfLink))
        }
        if (option.tips != null) {
            withAnnotation(
                tag = "PROMPT",
                annotation = option.id,
            ) {
                withStyle(
                    style = linkStyle
                ) {
                    if (option.prompt.contains('*')) {
                        append(option.prompt.substring(startOfLink + 1, endOfLink))
                    } else {
                        append(option.prompt)
                    }
                }
            }
        } else {
            withStyle(
                style = highlightedStyle
            ) {
                if (option.prompt.contains('*')) {
                    append(option.prompt.substring(startOfLink + 1, endOfLink))
                } else {
                    append(option.prompt)
                }
            }
        }
        suffix?.let {
            withStyle(
                style = textStyle,
            ) {
                append(suffix)
            }
        }
    }
}