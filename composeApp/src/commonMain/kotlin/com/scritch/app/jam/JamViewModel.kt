package com.scritch.app.jam

import androidx.core.uri.UriUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scritch.app.categories.Category
import com.scritch.app.categories.CategoryRepository
import com.scritch.app.categories.OptionState
import com.scritch.app.jam.data.JamRepository
import com.scritch.app.prompt.PromptViewState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class JamViewModel(
    private val jamRepository: JamRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val mutableViewState = MutableStateFlow(JamViewState.EMPTY)
    val viewState = mutableViewState.asStateFlow()

    init {
        viewModelScope.launch {
            loadJamData()
        }
    }

    private suspend fun loadJamData() {
        mutableViewState.update {
            it.copy(
                loadingState = when (it.loadingState) {
                    LoadingState.INITIAL_LOADING -> LoadingState.INITIAL_LOADING
                    LoadingState.LOADED -> LoadingState.REFRESHING
                    LoadingState.NO_JAM -> LoadingState.REFRESHING
                    LoadingState.REFRESHING -> LoadingState.REFRESHING
                }
            )
        }

        val jamDto = jamRepository.loadCurrentJam()

        if (jamDto == null) {
            mutableViewState.update {
                JamViewState.EMPTY.copy(
                    loadingState = LoadingState.NO_JAM,
                )
            }
        } else {
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

            val submission = Firebase.auth.currentUser?.uid?.let { uid ->
                jamRepository.getUserSubmission(
                    jamId = jamDto.id,
                    uid = Firebase.auth.currentUser?.uid ?: return,
                )
            }

            mutableViewState.update {
                it.copy(
                    loadingState = LoadingState.LOADED,
                    jamId = jamDto.id,
                    endDate = jamDto.endDate?.toLocalDateTime(TimeZone.currentSystemDefault()),
                    promptViewState = PromptViewState(
                        topic = topic,
                        medium = medium,
                        support = support,
                        constraint = constraint,
                        selectedOption = null,
                    ),
                    submissionState = if (submission == null || submission.imageUrl == null) {
                        SubmissionViewState.NotSubmitted
                    } else {
                        SubmissionViewState.Submitted(
                            imageUrl = submission.imageUrl,
                            status = submissionStatusFromString(submission.status) ?: throw IllegalStateException("Missing submission status")
                        )
                    }
                )
            }

            loadJamFeed()
        }
    }

    private suspend fun loadJamFeed() {
        val feed = jamRepository.getSubmissions(
            userId = Firebase.auth.currentUser?.uid ?: return,
            jamId = mutableViewState.value.jamId ?: return,
        )
        mutableViewState.update {
            it.copy(
                feedState = JamFeedState(
                    isLoading = false,
                    items = feed.items.mapNotNull { dto -> JamSubmission.fromDto(dto) },
                    cursor = feed.cursor,
                    endReached = feed.endReached,
                    error = null,
                ),
            )
        }
    }

    fun onLoadMore() {
        if (mutableViewState.value.feedState.isLoading || mutableViewState.value.feedState.endReached) return
        viewModelScope.launch {
            val nextPage = jamRepository.getSubmissions(
                userId = Firebase.auth.currentUser?.uid ?: return@launch,
                jamId = mutableViewState.value.jamId ?: return@launch,
                cursor = mutableViewState.value.feedState.cursor,
            )
            mutableViewState.update {
                it.copy(
                    feedState = it.feedState.copy(
                        isLoading = false,
                        items = it.feedState.items + nextPage.items.mapNotNull { dto ->
                            JamSubmission.fromDto(dto)
                        },
                        cursor = nextPage.cursor,
                        endReached = nextPage.endReached,
                    )
                )
            }
        }
    }

    fun onCategoryClick(clickedOptionId: String) {
        mutableViewState.update {
            it.copy(
                promptViewState = it.promptViewState.copy(
                    selectedOption = when {
                        it.promptViewState.medium?.id == clickedOptionId -> it.promptViewState.medium
                        it.promptViewState.constraint?.id == clickedOptionId -> it.promptViewState.constraint
                        it.promptViewState.support?.id == clickedOptionId -> it.promptViewState.support
                        it.promptViewState.topic?.id == clickedOptionId -> it.promptViewState.topic
                        else -> null
                    }
                )
            )
        }
    }

    fun onTipDisplayed() {
        mutableViewState.update {
            it.copy(
                promptViewState = it.promptViewState.copy(
                    selectedOption = null,
                ),
            )
        }
    }

    fun onSubmitWork() {
        mutableViewState.update {
            it.copy(
                dialog = JamScreenDialog.ImageSourceSheet,
            )
        }
    }

    fun onCameraSelectedAsSource() {
        mutableViewState.update {
            it.copy(
                dialog = null,
            )
        }
    }

    fun onGallerySelectedAsSource() {
        mutableViewState.update {
            it.copy(
                dialog = JamScreenDialog.GalleryPicker,
            )
        }
    }

    fun onRemoveSubmission() {
        if (viewState.value.dialog != JamScreenDialog.SubmissionDeleteConfirmation) {
            mutableViewState.update {
                it.copy(
                    dialog = JamScreenDialog.SubmissionDeleteConfirmation,
                )
            }
        } else {
            viewModelScope.launch {
                jamRepository.deleteWeeklyJamSubmission(
                    jamId = viewState.value.jamId ?: return@launch,
                    uid = Firebase.auth.currentUser?.uid ?: return@launch,
                )
                mutableViewState.update {
                    it.copy(
                        dialog = null,
                        submissionState = SubmissionViewState.NotSubmitted,
                    )
                }
            }
        }
    }

    fun onRetryUpload() {
        (viewState.value.submissionState as? SubmissionViewState.ImageTakenLocally)?.let {
            viewModelScope.launch {
                uploadSubmission(it.imageUri.toString())
            }
        }
    }

    fun onShowPreview() {
        mutableViewState.update {
            it.copy(
                dialog = JamScreenDialog.EntryPreview,
            )
        }
    }

    fun onDismissDialog(
        dialog: JamScreenDialog,
    ) {
        mutableViewState.update {
            it.copy(
                dialog = when (dialog) {
                    JamScreenDialog.EntryPreview -> null
                    JamScreenDialog.SubmissionDeleteConfirmation -> null
                    JamScreenDialog.ImageSourceSheet -> null
                    JamScreenDialog.GalleryPicker -> null
                },
            )
        }
    }

    fun onImageCaptured(imagePath: String) {
        viewModelScope.launch {
            uploadSubmission(imagePath)
        }
    }

    private suspend fun uploadSubmission(
        imagePath: String,
    ) {
        val imageUri = UriUtils.parse(imagePath)
        try {
            mutableViewState.update {
                it.copy(
                    submissionState = SubmissionViewState.ImageTakenLocally(
                        imageUri = imageUri,
                        uploadStatus = SubmissionUploadState.Uploading(null),
                    ),
                )
            }
            val submission = jamRepository.submitWeeklyJamImageResumable(
                jamId = viewState.value.jamId ?: return,
                uid = Firebase.auth.currentUser?.uid ?: return,
                pathOrUri = imagePath,
                onProgress = { pct ->
                    mutableViewState.update {
                        it.copy(
                            submissionState = SubmissionViewState.ImageTakenLocally(
                                imageUri = imageUri,
                                uploadStatus = SubmissionUploadState.Uploading(pct.div(100f)),
                            )
                        )
                    }
                }
            )

            mutableViewState.update {
                it.copy(
                    submissionState = SubmissionViewState.Submitted(
                        imageUrl = submission.imageUrl
                            ?: throw IllegalStateException("Missing image url"),
                        status = submissionStatusFromString(submission.status)
                            ?: throw IllegalStateException("Missing submission status"),
                    )
                )
            }
        } catch (exception: Exception) {
            mutableViewState.update {
                it.copy(
                    submissionState = SubmissionViewState.ImageTakenLocally(
                        imageUri = imageUri,
                        uploadStatus = SubmissionUploadState.Error(exception),
                    )
                )
            }
        }
    }
}