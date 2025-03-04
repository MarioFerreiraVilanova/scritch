package com.scritch.app.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.scritch.app.categories.Category
import com.scritch.app.categories.OptionState
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.arrows_repeat
import scritch.composeapp.generated.resources.scritch_logo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    val prompt = promptFromViewState(
        viewState = viewState,
        onClick = { category ->
            viewModel.onCategoryClick(category)
        }
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
                            text = "Scritch",
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
                                contentDescription = "Settings",
                            )
                        }
                    )
                }
            )
        },
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
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
                Button(
                    content = {
                        Icon(
                            painter = painterResource(Res.drawable.arrows_repeat),
                            contentDescription = null,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Generate prompt")
                    },
                    onClick = {
                        viewModel.onGeneratePrompt()
                    },
                    shape = MaterialTheme.shapes.small
                )
            }
        }
    }

    if (viewState.selectedOption != null) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                viewModel.onTipDisplayed()
            }
        ) {
            Tips(
                title = viewState.selectedOption?.name ?: "",
                description = viewState.selectedOption?.tips ?: ""
            )
        }
    }

}

@Composable
private fun promptFromViewState(
    viewState: HomeScreenViewState,
    onClick: (OptionState) -> Unit,
): AnnotatedString? {
    if (viewState.medium == null && viewState.support == null) return null


    return buildAnnotatedString {
        appendCategory(
            category = Category.Topic,
            option = viewState.topic,
            onClick = onClick,
        )
        append(", ")
        appendCategory(
            category = Category.Support,
            option = viewState.support,
            onClick = onClick,
        )
        append(", ")
        appendCategory(
            category = Category.Medium,
            option = viewState.medium,
            onClick = onClick,
        )
        append(". ")
        appendCategory(
            category = Category.Constraint,
            option = viewState.constraint,
            onClick = onClick,
        )
    }
}

@Composable
private fun AnnotatedString.Builder.appendCategory(
    category: Category,
    option: OptionState?,
    onClick: (OptionState) -> Unit,
) {
    val textStyle = MaterialTheme.typography.headlineMedium.toSpanStyle()
    val highlightedStyle = textStyle.copy(
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
    )
    val linkStyle = highlightedStyle.copy(
        textDecoration = TextDecoration.Underline,
    )

    if (option == null) {
        when (category) {
            Category.Medium -> append("with the medium of your choice")
            Category.Support -> append("on a surface of your choice")
            Category.Topic -> append("something")
            Category.Constraint -> {}
        }
    } else {
        val startOfLink = option.prompt.indexOfFirst { it == '*' }.coerceAtLeast(0)
        val endOfLink = option.prompt.indexOfLast { it == '*' }.coerceAtLeast(0)
        append(option.prompt.substring(0, startOfLink))
        if (option.tips != null) {
            withLink(
                link = LinkAnnotation.Clickable(
                    tag = category.name,
                    linkInteractionListener = {
                        onClick(option)
                    }
                )
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

    }
}