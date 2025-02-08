package com.scritch.app.wizard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.scritch.app.categories.Category
import com.scritch.app.uicomponents.PageHeader
import org.koin.compose.viewmodel.koinViewModel

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
        onBackClick = onBackClick,
        onContinue = { onContinue(viewState.step) },
    )
}

@Composable
private fun WizardScreen (
    viewState: WizardScreenViewState,
    onBackClick: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
){
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                            )
                        },
                        onClick = onBackClick,
                    )
                },
                title = {}
            )
        }
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
        ) {
            CategoryItems(
                viewState = viewState,
                modifier = Modifier.weight(1f),
            )
            NextStep(
                currentStep = viewState.step,
                onContinue = onContinue,
            )
        }
    }
}

@Composable
private fun CategoryItems(
    viewState: WizardScreenViewState,
    modifier: Modifier = Modifier,
){
    LazyColumn (
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
    ) {
        item {
            PageHeader(
                title = viewState.category.toTitle(),
                description = viewState.category.toDescription(),
            )
        }

        if (viewState.options == null){
            PageLoader()
        } else {
            items(
                items = viewState.options,
            ){

            }
        }
    }
}

@Composable
private fun NextStep(
    currentStep: Int,
    onContinue: () -> Unit,
){
    Surface (
        modifier = Modifier.fillMaxWidth(),
        elevation = 8.dp,
        color = MaterialTheme.colors.primarySurface,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ){
            Text(text = "$currentStep/2")
            Button(
                onClick = onContinue,
                content = {
                    Text("Continue")
                }
            )
        }
    }
}

private fun Category.toTitle() = when (this){
    Category.Medium -> "Mediums"
    Category.Support -> "Supports"
}

private fun Category.toDescription() = when (this){
    Category.Medium -> buildAnnotatedString {
        append("Scritch will prompt you for using different mediums. ")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("Unselect mediums you don't have or you don't want. ")
        }
        append("You won't see them in the prompts. You can always change that later in settings")
    }
    Category.Support -> buildAnnotatedString {
        append("Scritch will prompt you for using different supports. ")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("Unselect supports you don't have or you don't want. ")
        }
        append("You won't see them in the prompts. You can always change that later in settings")
    }
}