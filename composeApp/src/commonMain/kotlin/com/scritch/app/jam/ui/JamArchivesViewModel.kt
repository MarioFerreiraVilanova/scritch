package com.scritch.app.jam.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scritch.app.jam.Cursor
import com.scritch.app.jam.data.JamRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JamArchivesViewModel(
    private val jamRepository: JamRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(JamArchivesViewState())
    val viewState: StateFlow<JamArchivesViewState> = _viewState.asStateFlow()

    private var cursor: Cursor? = null

    init {
        loadJams()
    }

    fun loadJams() {
        if (_viewState.value.isLoadingMore || _viewState.value.endReached) return

        viewModelScope.launch {
            try {
                if (_viewState.value.jams.isEmpty()) {
                    _viewState.value = _viewState.value.copy(isLoading = true, error = null)
                } else {
                    _viewState.value = _viewState.value.copy(isLoadingMore = true, error = null)
                }

                val page = jamRepository.getPastJams(cursor = cursor, pageSize = 20)

                _viewState.value = _viewState.value.copy(
                    jams = _viewState.value.jams + page.items,
                    endReached = page.endReached,
                    isLoading = false,
                    isLoadingMore = false
                )

                cursor = page.cursor

            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = e.message ?: "Failed to load jam archives"
                )
            }
        }
    }

    fun onDismissError() {
        _viewState.value = _viewState.value.copy(error = null)
    }

    fun userParticipatedInJam(jamId: String): Boolean {
        val currentUserId = Firebase.auth.currentUser?.uid ?: return false
        val jam = _viewState.value.jams.find { it.id == jamId } ?: return false
        return jamRepository.userParticipatedInJam(jam, currentUserId)
    }
}