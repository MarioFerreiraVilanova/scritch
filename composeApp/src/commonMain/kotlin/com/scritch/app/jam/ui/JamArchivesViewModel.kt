package com.scritch.app.jam.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scritch.app.categories.Category
import com.scritch.app.categories.CategoryRepository
import com.scritch.app.categories.OptionState
import com.scritch.app.jam.Cursor
import com.scritch.app.jam.data.JamDto
import com.scritch.app.jam.data.JamRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

class JamArchivesViewModel(
    private val jamRepository: JamRepository,
    private val categoryRepository: CategoryRepository,
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
                    _viewState.update { it.copy(isLoading = true, error = null) }
                } else {
                    _viewState.update { it.copy(isLoadingMore = true, error = null) }
                }

                val page = jamRepository.getPastJams(cursor = cursor, pageSize = 20)

                val archiveJamViewStates = page.items.map { jamDto ->
                    convertJamDtoToViewState(jamDto)
                }

                _viewState.update { state ->
                    val newList = state.jams + archiveJamViewStates
                    state.copy(
                        jams = newList.distinctBy { it.id },
                        endReached = page.endReached,
                        isLoading = false,
                        isLoadingMore = false
                    )
                }

                cursor = page.cursor

            } catch (e: Exception) {
                _viewState.update {
                    it.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = e.message ?: "Failed to load jam archives"
                    )
                }
            }
        }
    }

    fun onDismissError() {
        _viewState.update { it.copy(error = null) }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun convertJamDtoToViewState(jamDto: JamDto): ArchiveJamViewState {
        val currentUserId = Firebase.auth.currentUser?.uid
        val userParticipated = currentUserId?.let { uid ->
            jamRepository.userParticipatedInJam(jamDto, uid)
        } ?: false

        val topic = jamDto.topic?.let { topicId ->
            categoryRepository.getOption(
                category = Category.Topic,
                optionId = topicId,
            )
        }?.let { topicDto ->
            OptionState.fromDto(
                dto = topicDto,
                selected = false,
            )
        }

        val medium = jamDto.medium?.let { mediumId ->
            categoryRepository.getOption(
                category = Category.Medium,
                optionId = mediumId,
            )
        }?.let { mediumDto ->
            OptionState.fromDto(
                dto = mediumDto,
                selected = false,
            )
        }

        val support = jamDto.support?.let { supportId ->
            categoryRepository.getOption(
                category = Category.Support,
                optionId = supportId,
            )
        }?.let { supportDto ->
            OptionState.fromDto(
                dto = supportDto,
                selected = false,
            )
        }

        val constraint = jamDto.constraint?.let { constraintId ->
            categoryRepository.getOption(
                category = Category.Constraint,
                optionId = constraintId,
            )
        }?.let { constraintDto ->
            OptionState.fromDto(constraintDto, false)
        }

        return ArchiveJamViewState(
            id = jamDto.id,
            startDate = jamDto.startDate?.toLocalDateTime(TimeZone.currentSystemDefault())?.date,
            endDate = jamDto.endDate?.toLocalDateTime(TimeZone.currentSystemDefault())?.date,
            topic = topic,
            medium = medium,
            support = support,
            constraint = constraint,
            submissionCount = jamDto.submissionCount,
            userParticipated = userParticipated,
        )
    }
}