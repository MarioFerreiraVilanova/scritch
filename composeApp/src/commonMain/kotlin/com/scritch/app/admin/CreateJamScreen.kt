package com.scritch.app.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scritch.app.categories.OptionState
import com.scritch.app.prompt.Prompt
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import scritch.composeapp.generated.resources.Res
import scritch.composeapp.generated.resources.april
import scritch.composeapp.generated.resources.august
import scritch.composeapp.generated.resources.back
import scritch.composeapp.generated.resources.constraint_optional
import scritch.composeapp.generated.resources.create_jam
import scritch.composeapp.generated.resources.create_new_jam
import scritch.composeapp.generated.resources.day
import scritch.composeapp.generated.resources.december
import scritch.composeapp.generated.resources.edit_jam_title
import scritch.composeapp.generated.resources.end_date
import scritch.composeapp.generated.resources.february
import scritch.composeapp.generated.resources.hour
import scritch.composeapp.generated.resources.jam_created_successfully
import scritch.composeapp.generated.resources.jam_name
import scritch.composeapp.generated.resources.jam_name_placeholder
import scritch.composeapp.generated.resources.jam_updated_successfully
import scritch.composeapp.generated.resources.january
import scritch.composeapp.generated.resources.july
import scritch.composeapp.generated.resources.june
import scritch.composeapp.generated.resources.march
import scritch.composeapp.generated.resources.may
import scritch.composeapp.generated.resources.medium
import scritch.composeapp.generated.resources.month
import scritch.composeapp.generated.resources.none
import scritch.composeapp.generated.resources.november
import scritch.composeapp.generated.resources.october
import scritch.composeapp.generated.resources.preview
import scritch.composeapp.generated.resources.prompt_elements
import scritch.composeapp.generated.resources.save_changes
import scritch.composeapp.generated.resources.september
import scritch.composeapp.generated.resources.start_date
import scritch.composeapp.generated.resources.support
import scritch.composeapp.generated.resources.topic_optional
import scritch.composeapp.generated.resources.year
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJamScreen(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateJamViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isEditMode = viewModel.isEditMode

    LaunchedEffect(viewState.error) {
        viewState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onDismissError()
        }
    }

    val successMessage = if (isEditMode) {
        stringResource(Res.string.jam_updated_successfully)
    } else {
        stringResource(Res.string.jam_created_successfully)
    }

    LaunchedEffect(viewState.isCreateSuccessful) {
        if (viewState.isCreateSuccessful) {
            snackbarHostState.showSnackbar(successMessage)
            onBackPress()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) {
                            stringResource(Res.string.edit_jam_title)
                        } else {
                            stringResource(Res.string.create_new_jam)
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (viewState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Jam Name
                OutlinedTextField(
                    value = viewState.jamName,
                    onValueChange = if (isEditMode) {
                        {}
                    } else {
                        viewModel::onJamNameChanged
                    },
                    readOnly = isEditMode,
                    label = { Text(stringResource(Res.string.jam_name)) },
                    placeholder = { Text(stringResource(Res.string.jam_name_placeholder)) },
                    isError = viewState.validationErrors.jamName != null,
                    supportingText = viewState.validationErrors.jamName?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                // Date Time Dropdowns
                DateTimeDropdownField(
                    label = stringResource(Res.string.start_date),
                    selectedDateTime = viewState.startDateTime,
                    onDateTimeSelected = viewModel::onStartDateTimeChanged,
                    error = viewState.validationErrors.startDate
                )

                DateTimeDropdownField(
                    label = stringResource(Res.string.end_date),
                    selectedDateTime = viewState.endDateTime,
                    onDateTimeSelected = viewModel::onEndDateTimeChanged,
                    error = viewState.validationErrors.endDate
                )

                // Category Selections
                Text(
                    text = stringResource(Res.string.prompt_elements),
                    style = MaterialTheme.typography.titleMedium,
                )

                CategoryDropdown(
                    label = stringResource(Res.string.topic_optional),
                    options = viewState.topicOptions,
                    selectedOption = viewState.selectedTopic,
                    onOptionSelected = viewModel::onTopicSelected
                )

                CategoryDropdown(
                    label = stringResource(Res.string.medium),
                    options = viewState.mediumOptions,
                    selectedOption = viewState.selectedMedium,
                    onOptionSelected = viewModel::onMediumSelected
                )

                CategoryDropdown(
                    label = stringResource(Res.string.support),
                    options = viewState.supportOptions,
                    selectedOption = viewState.selectedSupport,
                    onOptionSelected = viewModel::onSupportSelected
                )

                CategoryDropdown(
                    label = stringResource(Res.string.constraint_optional),
                    options = viewState.constraintOptions,
                    selectedOption = viewState.selectedConstraint,
                    onOptionSelected = viewModel::onConstraintSelected
                )

                viewState.validationErrors.categories?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Prompt Preview
                if (viewState.promptViewState.valid) {
                    HorizontalDivider()

                    Text(
                        text = stringResource(Res.string.preview),
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Prompt(
                            viewState = viewState.promptViewState,
                            onCategoryClick = { /* No action needed in preview */ },
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Create/Save Button
                Button(
                    onClick = viewModel::onCreateJam,
                    enabled = viewState.isFormValid && !viewState.isCreating &&
                            (if (isEditMode) viewState.hasChanges else true),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (viewState.isCreating) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text(
                            stringResource(
                                when {
                                    isEditMode -> Res.string.save_changes
                                    else -> Res.string.create_jam
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun DateTimeDropdownField(
    label: String,
    selectedDateTime: LocalDateTime?,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    error: String? = null,
) {
    val currentYear = remember {
        Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .year
    }

    val years = remember { listOf(currentYear, currentYear + 1) }
    val months = listOf(
        1 to stringResource(Res.string.january),
        2 to stringResource(Res.string.february),
        3 to stringResource(Res.string.march),
        4 to stringResource(Res.string.april),
        5 to stringResource(Res.string.may),
        6 to stringResource(Res.string.june),
        7 to stringResource(Res.string.july),
        8 to stringResource(Res.string.august),
        9 to stringResource(Res.string.september),
        10 to stringResource(Res.string.october),
        11 to stringResource(Res.string.november),
        12 to stringResource(Res.string.december)
    )

    val selectedYear = selectedDateTime?.year ?: currentYear
    val selectedMonth = selectedDateTime?.month?.ordinal?.plus(1) ?: 1
    val selectedDay = selectedDateTime?.day ?: 1
    val selectedHour = selectedDateTime?.hour ?: 0

    fun getDaysInMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
            else -> 31
        }
    }

    val daysInSelectedMonth = getDaysInMonth(selectedYear, selectedMonth)
    val days = remember(selectedYear, selectedMonth) {
        (1..daysInSelectedMonth).toList()
    }

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Year Dropdown
            SimpleDropdown(
                label = stringResource(Res.string.year),
                options = years.map { it.toString() },
                selectedOption = selectedYear.toString(),
                onOptionSelected = { yearString ->
                    val newYear = yearString.toInt()
                    val newDaysInMonth = getDaysInMonth(newYear, selectedMonth)
                    val newDay = if (selectedDay > newDaysInMonth) newDaysInMonth else selectedDay
                    onDateTimeSelected(
                        LocalDateTime(
                            newYear,
                            selectedMonth,
                            newDay,
                            selectedHour,
                            0
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            )

            // Month Dropdown
            SimpleDropdown(
                label = stringResource(Res.string.month),
                options = months.map { it.second },
                selectedOption = months.find { it.first == selectedMonth }?.second ?: "January",
                onOptionSelected = { monthName ->
                    val newMonth = months.find { it.second == monthName }?.first ?: 1
                    val newDaysInMonth = getDaysInMonth(selectedYear, newMonth)
                    val newDay = if (selectedDay > newDaysInMonth) newDaysInMonth else selectedDay
                    onDateTimeSelected(
                        LocalDateTime(
                            selectedYear,
                            newMonth,
                            newDay,
                            selectedHour,
                            0
                        )
                    )
                },
                modifier = Modifier.weight(2f)
            )

            // Day Dropdown
            SimpleDropdown(
                label = stringResource(Res.string.day),
                options = days.map { it.toString() },
                selectedOption = selectedDay.toString(),
                onOptionSelected = { dayString ->
                    val newDay = dayString.toInt()
                    onDateTimeSelected(
                        LocalDateTime(
                            selectedYear,
                            selectedMonth,
                            newDay,
                            selectedHour,
                            0
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            )

            // Hour Dropdown
            SimpleDropdown(
                label = stringResource(Res.string.hour),
                options = (0..23).map { it.toString().padStart(2, '0') },
                selectedOption = selectedHour.toString().padStart(2, '0'),
                onOptionSelected = { hourString ->
                    val newHour = hourString.toInt()
                    onDateTimeSelected(
                        LocalDateTime(
                            selectedYear,
                            selectedMonth,
                            selectedDay,
                            newHour,
                            0
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimpleDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    label: String,
    options: List<OptionState>,
    selectedOption: OptionState?,
    onOptionSelected: (OptionState?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selectedOption?.name ?: "",
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // None option for optional fields
            if (label.contains("Optional")) {
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.none)) },
                    onClick = {
                        onOptionSelected(null)
                        expanded = false
                    }
                )
            }

            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}