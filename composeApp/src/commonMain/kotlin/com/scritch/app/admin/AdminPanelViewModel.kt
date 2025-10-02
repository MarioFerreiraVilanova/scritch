package com.scritch.app.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminPanelViewModel(
    private val adminRepository: AdminRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(AdminPanelViewState.EMPTY)
    val viewState: StateFlow<AdminPanelViewState> = _viewState.asStateFlow()

    fun batchGenerateThumbnails() {
        viewModelScope.launch {
            _viewState.update {
                it.copy(
                    isGeneratingThumbnails = true,
                    message = null,
                    error = null
                )
            }

            try {
                val result = adminRepository.batchGenerateThumbnails()

                if (result.success) {
                    _viewState.update {
                        it.copy(
                            isGeneratingThumbnails = false,
                            message = "Thumbnail generation completed: ${result.processedCount} generated, ${result.errorCount} errors",
                            error = null
                        )
                    }
                } else {
                    _viewState.update {
                        it.copy(
                            isGeneratingThumbnails = false,
                            message = null,
                            error = "Failed to generate thumbnails: ${result.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                _viewState.update {
                    it.copy(
                        isGeneratingThumbnails = false,
                        message = null,
                        error = "Error generating thumbnails: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _viewState.update {
            it.copy(message = null, error = null)
        }
    }
}