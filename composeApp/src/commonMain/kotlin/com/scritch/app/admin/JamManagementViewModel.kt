package com.scritch.app.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scritch.app.jam.Cursor
import com.scritch.app.jam.data.JamDto
import com.scritch.app.jam.data.JamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JamManagementViewModel(
    private val jamRepository: JamRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(JamManagementViewState.EMPTY)
    val viewState: StateFlow<JamManagementViewState> = _viewState.asStateFlow()

    init {
        loadJams()
    }

    fun loadJams(refresh: Boolean = false) {
        val currentState = _viewState.value

        if (refresh) {
            _viewState.value = JamManagementViewState.EMPTY.copy(isLoading = true)
            loadJamsInternal(null)
        } else if (!currentState.isLoading && !currentState.endReached) {
            _viewState.value = currentState.copy(isLoadingMore = true)
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

                _viewState.value = currentState.copy(
                    jams = newJams,
                    cursor = page.cursor,
                    endReached = page.endReached,
                    isLoading = false,
                    isLoadingMore = false,
                    error = null,
                )
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = "Failed to load jams: ${e.message}"
                )
            }
        }
    }

    fun onDismissError() {
        _viewState.value = _viewState.value.copy(error = null)
    }

    fun deleteJam(jamId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                jamRepository.deleteJam(jamId)

                // Remove the jam from the local list
                val currentState = _viewState.value
                val updatedJams = currentState.jams.filter { it.id != jamId }
                _viewState.value = currentState.copy(jams = updatedJams)

                onSuccess()
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(
                    error = "Failed to delete jam: ${e.message}"
                )
            }
        }
    }
}