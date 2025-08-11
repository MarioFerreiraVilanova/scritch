package com.scritch.app.jam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scritch.app.categories.Category
import com.scritch.app.categories.CategoryRepository
import com.scritch.app.categories.OptionState
import com.scritch.app.prompt.PromptViewState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import io.github.ismoy.imagepickerkmp.CameraPhotoHandler
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
                    LoadingState.LOADING -> LoadingState.LOADING
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
            }?.let { constraintDto -> OptionState.fromDto(constraintDto, false) }
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
                showCamera = true,
            )
        }
    }

    fun onRemoveSubmission(){
        //TODO show a dialog asking for confirmation first
        mutableViewState.update {
            it.copy(
                submissionState = SubmissionViewState.NotSubmitted,
            )
        }
    }

    fun onShowPreview(){
        mutableViewState.update {
            it.copy(
                dialog = JamScreenDialog.SubmissionPreview,
            )
        }
    }

    fun onDismissDialog(){
        mutableViewState.update {
            it.copy(
                dialog = null,
            )
        }
    }

    fun onImageCaptureError(e: Exception) {
        mutableViewState.update {
            it.copy(
                showCamera = false,
            )
        }
    }

    fun onImageCaptured(result: CameraPhotoHandler.PhotoResult) {
        viewModelScope.launch {
            uploadSubmission(result)
        }
    }

    fun onImageCaptureDismiss() {
        mutableViewState.update {
            it.copy(
                showCamera = false,
            )
        }
    }

    private suspend fun uploadSubmission(
        image: CameraPhotoHandler.PhotoResult,
    ){
        try{
            mutableViewState.update {
                it.copy(
                    submissionState = SubmissionViewState.ImageTakenLocally(
                        image = image,
                        uploadStatus = SubmissionUploadState.Uploading(null),
                    ),
                    showCamera = false,
                )
            }
            jamRepository.submitWeeklyJamImageResumable(
                jamId = viewState.value.jamId ?: return,
                uid = Firebase.auth.currentUser?.uid ?: return,
                pathOrUri = image.uri,
                onProgress = { pct ->
                    mutableViewState.update {
                        it.copy(
                            submissionState = SubmissionViewState.ImageTakenLocally(
                                image = image,
                                uploadStatus = SubmissionUploadState.Uploading(pct.toFloat()),
                            )
                        )
                    }
                }
            )
            mutableViewState.update {
                it.copy(
                    submissionState = SubmissionViewState.ImageTakenLocally(
                        image = image,
                        uploadStatus = SubmissionUploadState.Success,
                    )
                )
            }
        }catch (exception: IllegalStateException){
            mutableViewState.update {
                it.copy(
                    submissionState = SubmissionViewState.ImageTakenLocally(
                        image = image,
                        uploadStatus = SubmissionUploadState.Error(exception),
                    )
                )
            }
        }
    }
}