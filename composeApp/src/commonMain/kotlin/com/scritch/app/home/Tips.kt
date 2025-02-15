package com.scritch.app.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Tips(
    title: String,
    description: String,
) {
    Column (
        modifier = Modifier.navigationBarsPadding()
    ) {
        TopAppBar(
            title = {
                Text("Prompt tips")
            }
        )
        Text(title)
        Text(description)
    }
}