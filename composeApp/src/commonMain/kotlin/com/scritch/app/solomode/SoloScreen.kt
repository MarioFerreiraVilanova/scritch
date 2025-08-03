package com.scritch.app.solomode

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.scritch.app.categories.OptionState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.app_name
import scritch.composeapp.generated.resources.arrows_repeat
import scritch.composeapp.generated.resources.generate_prompt
import scritch.composeapp.generated.resources.scritch_logo
import scritch.composeapp.generated.resources.settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoloScreen(
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SoloViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    val prompt = promptFromViewState(
        viewState = viewState
    )

    LaunchedEffect(viewState.selectedOption) {
        if (viewState.selectedOption != null) {
            sheetState.show()
        }
    }
    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible) {
            viewModel.onTipDisplayed()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) run {
            viewModel.onResume()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.scritch_logo),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(Res.string.app_name),
                            style = MaterialTheme.typography.headlineSmall
                                .copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onGoToSettings,
                        content = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(Res.string.settings),
                            )
                        }
                    )
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = prompt != null,
                enter = slideInVertically(
                    initialOffsetY = { height -> height }
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    PromptButton(
                        modifier = Modifier.padding(16.dp),
                        onClick = viewModel::onGeneratePrompt
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                if (prompt != null) {
                    ClickableText(
                        text = prompt,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(
                            bottom = 64.dp
                        )
                    ) { offset ->
                        prompt.getStringAnnotations(offset, offset).firstOrNull()?.item?.let {
                            viewModel.onCategoryClick(it)
                        }
                    }
                } else {
                    PromptButton(
                        onClick = viewModel::onGeneratePrompt
                    )
                }
            }
        }
    }

    if (viewState.selectedOption != null) {
        ModalBottomSheet(
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            onDismissRequest = {
                viewModel.onTipDisplayed()
            },
        ) {
            viewState.selectedOption?.tips?.let { tips ->
                Tips(
                    tips = tips
                )
            }
        }
    }

}

@Composable
private fun promptFromViewState(
    viewState: SoloViewState,
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

@Composable
private fun PromptButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier,
        content = {
            Icon(
                painter = painterResource(Res.drawable.arrows_repeat),
                contentDescription = null,
            )
            Spacer(Modifier.width(8.dp))
            Text(text = stringResource(Res.string.generate_prompt))
        },
        onClick = onClick,
        shape = MaterialTheme.shapes.small
    )
}