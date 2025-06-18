package com.scritch.app.wizard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.scritch.app.categories.Category
import com.scritch.app.categories.Option
import com.scritch.app.categories.toTitle
import com.scritch.app.uicomponents.PageHeader
import com.scritch.app.uicomponents.PageLoader
import com.scritch.app.uicomponents.ToggleListRow
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.back
import scritch.composeapp.generated.resources.category_art_medium
import scritch.composeapp.generated.resources.category_constraint
import scritch.composeapp.generated.resources.category_description_3
import scritch.composeapp.generated.resources.category_description_constraint_1
import scritch.composeapp.generated.resources.category_description_constraint_2
import scritch.composeapp.generated.resources.category_description_medium_1
import scritch.composeapp.generated.resources.category_description_medium_2
import scritch.composeapp.generated.resources.category_description_support_1
import scritch.composeapp.generated.resources.category_description_support_2
import scritch.composeapp.generated.resources.category_description_topic_1
import scritch.composeapp.generated.resources.category_description_topic_2
import scritch.composeapp.generated.resources.category_support
import scritch.composeapp.generated.resources.category_topic
import scritch.composeapp.generated.resources.continue_word
import scritch.composeapp.generated.resources.no_category_imposed
import scritch.composeapp.generated.resources.unselect_all
import scritch.composeapp.generated.resources.unselect_options_description

@Composable
fun WizardScreen(
    onBackClick: () -> Unit,
    onContinue: (currentStep: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WizardScreenViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()
    WizardScreen(
        viewState = viewState,
        modifier = modifier,
        onBackClick = {
            viewModel.onBackClick()
            onBackClick()
        },
        onContinue = {
            viewModel.onContinue()
            viewState.step?.let { step ->
                onContinue(step)
            }
        },
        onOptionCheckChangeRequest = viewModel::onOptionCheckChangeRequest,
        onUnselectAll = viewModel::onUnselectAll,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WizardScreen(
    viewState: WizardScreenViewState,
    onBackClick: () -> Unit,
    onContinue: () -> Unit,
    onOptionCheckChangeRequest: (optionId: String) -> Unit,
    onUnselectAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.back),
                            )
                        },
                        onClick = onBackClick,
                    )
                },
                title = {
                    if (viewState.step == null) {
                        Text(viewState.category.toTitle())
                    }
                }
            )
        },
        bottomBar = {
            if (viewState.step != null) {
                NextStep(
                    currentStep = viewState.step,
                    onContinue = onContinue,
                )
            }
        }
    ) { innerPadding ->
        CategoryItems(
            viewState = viewState,
            modifier = Modifier.fillMaxSize().consumeWindowInsets(innerPadding),
            onCheckChangeRequest = onOptionCheckChangeRequest,
            onUnselectAll = onUnselectAll,
            innerPadding = innerPadding,
        )
    }
}

@Composable
private fun CategoryItems(
    viewState: WizardScreenViewState,
    innerPadding: PaddingValues,
    onCheckChangeRequest: (optionId: String) -> Unit,
    onUnselectAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = innerPadding,
    ) {
        item {
            Header(
                viewState = viewState,
            )
        }

        item {
            ToggleListRow(
                title = stringResource(Res.string.unselect_all),
                subtitle = stringResource(
                    resource = Res.string.no_category_imposed,
                    viewState.category.toTitle().lowercase(),
                ),
                checked = viewState.unselectAll,
                onCheckChangeRequest = onUnselectAll,
            )
        }

        item {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (viewState.optionStates == null) {
            item {
                PageLoader()
            }
        } else {
            items(
                count = viewState.optionStates.size,
                key = { index -> viewState.optionStates[index].id }
            ) { index ->
                viewState.optionStates[index].let { optionState ->
                    Option(
                        state = optionState,
                        enabled = !viewState.unselectAll,
                        onCheckChangeRequest = { onCheckChangeRequest(optionState.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(
    viewState: WizardScreenViewState,
) {
    if (viewState.isWizard) {
        PageHeader(
            modifier = Modifier.padding(16.dp),
            title = viewState.category.toTitle(),
            description = viewState.category.toDescription(),
        )
    } else {
        val categoryText = viewState.category.toTitle(plural = true).toLowerCase(LocaleList.current)
        Text(
            text = stringResource(Res.string.unselect_options_description, categoryText),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun NextStep(
    currentStep: Int,
    onContinue: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.navigationBarsPadding().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "$currentStep/${Category.entries.size}")
            Button(
                onClick = onContinue,
                content = {
                    Text(stringResource(Res.string.continue_word))
                }
            )
        }
    }
}

@Composable
private fun Category.toDescription() = when (this) {
    Category.Medium -> buildAnnotatedString {
        append(stringResource(Res.string.category_description_medium_1))
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(stringResource(Res.string.category_description_medium_2))
        }
        append(stringResource(Res.string.category_description_3))
    }

    Category.Support -> buildAnnotatedString {
        append(stringResource(Res.string.category_description_support_1))
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(stringResource(Res.string.category_description_support_2))
        }
        append(stringResource(Res.string.category_description_3))
    }

    Category.Topic -> buildAnnotatedString {
        append(stringResource(Res.string.category_description_topic_1))
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(stringResource(Res.string.category_description_topic_2))
        }
        append(stringResource(Res.string.category_description_3))
    }

    Category.Constraint -> buildAnnotatedString {
        append(stringResource(Res.string.category_description_constraint_1))
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(stringResource(Res.string.category_description_constraint_2))
        }
        append(stringResource(Res.string.category_description_3))
    }
}