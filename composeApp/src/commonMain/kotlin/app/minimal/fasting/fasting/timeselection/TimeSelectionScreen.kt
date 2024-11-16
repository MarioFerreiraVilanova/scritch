@file:OptIn(ExperimentalLayoutApi::class)

package app.minimal.fasting.fasting.timeselection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
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
    Box(
        modifier = modifier.fillMaxSize()
            .background(
                MinimalTheme.color.surface01,
            )
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            BasicText(
                text = "Placeholder",
                style = MinimalTheme.typography.h3.copy(
                    color = MinimalTheme.color.typography2,
                ),
            )
            HourPicker(
                hint = viewState.hintHour,
                selection = viewState.selectedHour,
                onHourPick = { selectedHour ->
                    viewModel.onHourSelect(selectedHour)
                }
            )
            MinutePicker(
                hint = viewState.hintMinute,
                selection = viewState.selectedMinute,
                onMinutePick = { selectedMinute ->
                    viewModel.onMinuteSelect(selectedMinute)
                }
            )
            TextButton(
                label = "done.",
                onClick = onDone,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }

}

@Composable
private fun HourPicker(
    hint: Int?,
    selection: Int?,
    modifier: Modifier = Modifier,
    onHourPick: (hour: Int) -> Unit,
) {
    val hours = remember {
        listOf(
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 21, 23, 24
        )
    }
    val labels = remember {
        hours.map { hour ->
            if (hour < 10) {
                "0$hour"
            } else {
                hour.toString()
            }
        }
    }

    Picker(
        modifier = modifier,
        items = labels,
        hint = hours.indexOf(hint),
        selection = hours.indexOf(selection),
        onSelected = { index -> onHourPick(hours[index]) }
    )
}

@Composable
private fun MinutePicker(
    hint: Int?,
    selection: Int?,
    modifier: Modifier = Modifier,
    onMinutePick: (hour: Int) -> Unit,
) {
    val minutes = remember {
        listOf(
            0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55
        )
    }
    val labels = remember {
        minutes.map { minute ->
            if (minute < 10) {
                "0$minute"
            } else {
                minute.toString()
            }
        }
    }

    Picker(
        modifier = modifier,
        items = labels,
        hint = minutes.indexOf(hint),
        selection = minutes.indexOf(selection),
        onSelected = { index -> onMinutePick(minutes[index]) }
    )
}

@Composable
private fun Picker(
    items: List<String>,
    hint: Int?,
    selection: Int?,
    modifier: Modifier = Modifier,
    onSelected: (index: Int) -> Unit,
) {
    FlowRow (
        modifier = modifier,
    ){
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
    Box(
        modifier = Modifier
            .size(48.dp)
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                when {
                    selected -> MinimalTheme.color.emphasis
                    hint -> MinimalTheme.color.surface02
                    else -> MinimalTheme.color.surface03
                }
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = label,
            style = MinimalTheme.typography.h6.copy(
                color = when {
                    selected -> MinimalTheme.color.onEmphasis
                    hint -> MinimalTheme.color.typography2
                    else -> MinimalTheme.color.typography
                }
            ),
        )
    }
}