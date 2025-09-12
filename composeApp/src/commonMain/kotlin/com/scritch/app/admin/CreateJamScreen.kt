package com.scritch.app.admin

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun CreateJamScreen(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateJamViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewState.error) {
        viewState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onDismissError()
        }
    }

    LaunchedEffect(viewState.isCreateSuccessful) {
        if (viewState.isCreateSuccessful) {
            snackbarHostState.showSnackbar("Jam created successfully!")
            onBackPress()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Create New Jam") },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
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
                    onValueChange = viewModel::onJamNameChanged,
                    label = { Text("Jam Name") },
                    placeholder = { Text("e.g., 2025-37") },
                    isError = viewState.validationErrors.jamName != null,
                    supportingText = viewState.validationErrors.jamName?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                // Date Pickers
                DatePickerField(
                    label = "Start Date",
                    selectedDate = viewState.startDate,
                    onDateSelected = viewModel::onStartDateChanged,
                    error = viewState.validationErrors.startDate
                )

                DatePickerField(
                    label = "End Date",
                    selectedDate = viewState.endDate,
                    onDateSelected = viewModel::onEndDateChanged,
                    error = viewState.validationErrors.endDate
                )

                // Category Selections
                Text(
                    text = "Prompt Elements",
                    style = MaterialTheme.typography.titleMedium,
                )

                CategoryDropdown(
                    label = "Topic (Optional)",
                    options = viewState.topicOptions,
                    selectedOption = viewState.selectedTopic,
                    onOptionSelected = viewModel::onTopicSelected
                )

                CategoryDropdown(
                    label = "Medium",
                    options = viewState.mediumOptions,
                    selectedOption = viewState.selectedMedium,
                    onOptionSelected = viewModel::onMediumSelected
                )

                CategoryDropdown(
                    label = "Support",
                    options = viewState.supportOptions,
                    selectedOption = viewState.selectedSupport,
                    onOptionSelected = viewModel::onSupportSelected
                )

                CategoryDropdown(
                    label = "Constraint (Optional)",
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
                        text = "Preview",
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

                // Create Button
                Button(
                    onClick = viewModel::onCreateJam,
                    enabled = viewState.isFormValid && !viewState.isCreating,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (viewState.isCreating) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Create Jam")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun DatePickerField(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    error: String? = null,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate?.let { date ->
            LocalDateTime(
                date.year, 
                date.month, 
                date.day, 
                0, 0, 0, 0
            ).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        }
    )

    OutlinedTextField(
        value = selectedDate?.toString() ?: "",
        onValueChange = { },
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showDatePicker)
        },
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        modifier = Modifier.fillMaxWidth(),
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            showDatePicker = true
                        }
                    }
                }
            }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.fromEpochMilliseconds(millis)
                            val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                            onDateSelected(localDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
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
                    text = { Text("None") },
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