package com.scritch.app.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scritch.app.jam.Cursor
import com.scritch.app.jam.data.JamDto
import com.scritch.app.jam.data.JamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class JamManagementViewModel(
    private val jamRepository: JamRepository,
    private val adminRepository: AdminRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(JamManagementViewState.EMPTY)
    val viewState: StateFlow<JamManagementViewState> = _viewState.asStateFlow()

    init {
        loadJams()
    }

    fun loadJams(refresh: Boolean = false) {
        val currentState = _viewState.value

        if (refresh) {
            _viewState.update { JamManagementViewState.EMPTY.copy(isLoading = true) }
            loadJamsInternal(null)
        } else if (!currentState.isLoading && !currentState.endReached) {
            _viewState.update { it.copy(isLoadingMore = true) }
            loadJamsInternal(currentState.cursor)
        }
    }

    private fun loadJamsInternal(cursor: Cursor?) {
        viewModelScope.launch {
            try {
                val page = jamRepository.getAllJams(cursor = cursor, pageSize = 20)

                val currentState = _viewState.value
                val newJams = if (cursor == null) {
                    page.items
                } else {
                    currentState.jams + page.items
                }

                _viewState.update {
                    it.copy(
                        jams = newJams,
                        cursor = page.cursor,
                        endReached = page.endReached,
                        isLoading = false,
                        isLoadingMore = false,
                        error = null,
                    )
                }
            } catch (e: Exception) {
                _viewState.update {
                    it.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = "Failed to load jams: ${e.message}"
                    )
                }
            }
        }
    }

    fun onDismissError() {
        _viewState.update { it.copy(error = null) }
    }

    fun deleteJam(jamId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                jamRepository.deleteJam(jamId)

                // Remove the jam from the local list
                _viewState.update { currentState ->
                    val updatedJams = currentState.jams.filter { it.id != jamId }
                    currentState.copy(jams = updatedJams)
                }

                onSuccess()
            } catch (e: Exception) {
                _viewState.update {
                    it.copy(error = "Failed to delete jam: ${e.message}")
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun recalculateJamStats(jamId: String) {
        viewModelScope.launch {
            try {
                val result = adminRepository.recalculateJamStats(jamId)

                if (result.success) {
                    // Update the local jam data with new stats
                    _viewState.update { currentState ->
                        val updatedJams = currentState.jams.map { jam ->
                            if (jam.id == jamId) {
                                jam.copy(
                                    submissionCount = result.submissionCount,
                                    participants = emptyList() // We don't get participant IDs back, just count
                                )
                            } else {
                                jam
                            }
                        }
                        currentState.copy(jams = updatedJams)
                    }

                    // Show success message
                    _viewState.update {
                        it.copy(error = "Stats recalculated: ${result.submissionCount} submissions, ${result.participantCount} participants")
                    }
                } else {
                    _viewState.update {
                        it.copy(error = result.message)
                    }
                }
            } catch (e: Exception) {
                _viewState.update {
                    it.copy(error = "Failed to recalculate stats: ${e.message}")
                }
            }
        }
    }
}