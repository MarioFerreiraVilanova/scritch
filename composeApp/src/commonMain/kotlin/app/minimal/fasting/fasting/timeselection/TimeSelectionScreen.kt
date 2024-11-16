@file:OptIn(ExperimentalLayoutApi::class)

package app.minimal.fasting.fasting.timeselection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.minimal.fasting.theme.MinimalTheme
import app.minimal.fasting.theme.components.TextButton
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TimeSelectionScreen(
    modifier: Modifier = Modifier,
    viewModel: TimeSelectionViewModel = koinViewModel(),
    onDone: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()
    /*if (viewState.done){
        onDone()
    }*/
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        BasicText(
            text = "Placeholder",
            style = MinimalTheme.typography.h3.copy(
                color = MinimalTheme.color.typography2,
            ),
        )
        when (viewState.step){
            TimeSelectionStep.Hours -> HourPicker(
                hint = viewState.hintHour,
                selection = viewState.selectedHour,
                onHourPick = { selectedHour ->
                    viewModel.onHourSelect(selectedHour)
                }
            )
            TimeSelectionStep.Minutes -> MinutePicker(
                hint = viewState.hintMinute,
                selection = viewState.selectedMinute,
                onMinutePick = { selectedMinute ->
                    viewModel.onMinuteSelect(selectedMinute)
                }
            )
        }
        TextButton(
            label = "done.",
            onClick = onDone,
            modifier = Modifier.align(Alignment.End),
        )
    }
}

@Composable
private fun HourPicker(
    hint: Int?,
    selection: Int?,
    onHourPick: (hour: Int) -> Unit,
){
    val hours = remember {
        listOf(
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 21, 23, 24
        )
    }
    val labels = remember {
        hours.map { hour ->
            if (hour < 10){
                "0$hour"
            } else {
                hour.toString()
            }
        }
    }

    Picker(
        items = labels,
        hint = hint,
        selection = selection,
        onSelected = { index ->  onHourPick(hours[index]) }
    )
}

@Composable
private fun MinutePicker(
    hint: Int?,
    selection: Int?,
    onMinutePick: (hour: Int) -> Unit,
){
    val minutes = remember {
        listOf(
            0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55
        )
    }
    val labels = remember {
        minutes.map { minute ->
            if (minute < 10){
                "0$minute"
            } else {
                minute.toString()
            }
        }
    }

    Picker(
        items = labels,
        hint = hint,
        selection = selection,
        onSelected = { index ->  onMinutePick(minutes[index]) }
    )
}

@Composable
private fun Picker(
    items: List<String>,
    hint: Int?,
    selection: Int?,
    onSelected: (index: Int) -> Unit,
){
    FlowRow {
        items.forEachIndexed { index, label ->
            PickerItem(
                label = label,
                hint = index == hint,
                selected = index == selection,
                onClick = { onSelected(index) }
            )
        }
    }
}

@Composable
private fun PickerItem(
    label: String,
    hint: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box {
        BasicText(
            text = label,
            style = MinimalTheme.typography.h6.copy(
                color = when {
                    selected -> MinimalTheme.color.surface04
                    hint -> MinimalTheme.color.surface03
                    else -> MinimalTheme.color.typography
                }
            ),
            modifier = Modifier.clickable {
                onClick()
            }
        )
    }
}