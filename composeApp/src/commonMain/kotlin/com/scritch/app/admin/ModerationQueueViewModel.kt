package com.scritch.app.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ModerationQueueViewModel(
    private val adminRepository: AdminRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(ModerationQueueViewState())
    val viewState: StateFlow<ModerationQueueViewState> = _viewState.asStateFlow()

    init {
        loadModerationQueue()
    }

    fun loadModerationQueue() {
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(isLoading = true, error = null)
            
            val response = adminRepository.getModerationQueue()
            
            if (response.success) {
                _viewState.value = _viewState.value.copy(
                    queue = response.queue,
                    isLoading = false,
                )
            } else {
                _viewState.value = _viewState.value.copy(
                    isLoading = false,
                    error = "Failed to load moderation queue"
                )
            }
        }
    }

    fun approveSubmission(jamId: String, userId: String) {
        moderateSubmission(jamId, userId, "approved", "Approved by admin")
    }

    fun rejectSubmission(jamId: String, userId: String) {
        moderateSubmission(jamId, userId, "rejected", "Rejected by admin")
    }

    private fun moderateSubmission(jamId: String, userId: String, status: String, reason: String) {
        val itemKey = "$jamId/$userId"
        
        viewModelScope.launch {
            // Add to processing set
            _viewState.value = _viewState.value.copy(
                processingItems = _viewState.value.processingItems + itemKey
            )

            val response = adminRepository.moderateSubmission(jamId, userId, status, reason)

            if (response.success) {
                // Remove the item from the queue since it's been processed
                val updatedQueue = _viewState.value.queue.filterNot { 
                    it.jamId == jamId && it.userId == userId 
                }
                _viewState.value = _viewState.value.copy(
                    queue = updatedQueue,
                    processingItems = _viewState.value.processingItems - itemKey
                )
            } else {
                // Remove from processing set but keep in queue
                _viewState.value = _viewState.value.copy(
                    processingItems = _viewState.value.processingItems - itemKey,
                    error = response.message
                )
            }
        }
    }

    fun clearError() {
        _viewState.value = _viewState.value.copy(error = null)
    }
}