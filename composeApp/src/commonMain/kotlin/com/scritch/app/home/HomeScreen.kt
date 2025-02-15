package com.scritch.app.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.scritch.app.categories.Category
import com.scritch.app.categories.OptionState
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    val prompt = promptFromViewState(
        viewState = viewState,
        onClick = { category ->
            viewModel.onCategoryClick(category)
        }
    )

    LaunchedEffect(viewState.selectedOption){
        if (viewState.selectedOption != null){
            sheetState.show()
        }
    }
    LaunchedEffect(sheetState.isVisible){
        if (!sheetState.isVisible){
            viewModel.onTipDisplayed()
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            Tips(
                title = viewState.selectedOption?.name ?: "",
                description = viewState.selectedOption?.description ?: ""
            )
        }
    ){
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = {
                        Text("Scritch")
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
        ) {
            Box(
                modifier = modifier.fillMaxSize(),
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
                            style = MaterialTheme.typography.h4,
                        )
                    }
                    Button(
                        content = { Text("Generate prompt") },
                        onClick = {
                            viewModel.onGeneratePrompt()
                        },
                    )
                }
            }
        }
    }

}

@Composable
private fun promptFromViewState(
    viewState: HomeScreenViewState,
    onClick: (OptionState) -> Unit,
): AnnotatedString? {
    if (viewState.medium == null && viewState.support == null) return null

    val textStyle = MaterialTheme.typography.h4.toSpanStyle()
    val linkStyle = textStyle.copy(
        textDecoration = TextDecoration.Underline
    )
    return buildAnnotatedString {
        append("Draw 5 objects")
        if (viewState.medium == null) {
            append("with a medium of your choice")
        } else {
            append("with a ")
            withLink(
                link = LinkAnnotation.Clickable(
                    tag = Category.Medium.name,
                    linkInteractionListener = {
                        onClick(viewState.medium)
                    }
                )
            ) {
                withStyle(
                    style = linkStyle
                ) {
                    append(viewState.medium.name)
                }
            }
            append(" ")
        }
    }

    /*val subjectText = "Draw 5 objects"
    val mediumText = if (viewState.medium == null) {
        "with a medium of your choice"
    } else {
        "with a ${viewState.medium.name}"
    }
    val supportText = if (viewState.support == null) {
        "with the support of your choice"
    } else {
        "on a ${viewState.support.name}"
    }

    return "$subjectText, $mediumText, $supportText"*/
}