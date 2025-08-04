package com.scritch.app.jam

import androidx.lifecycle.ViewModel
import com.scritch.app.categories.Category
import com.scritch.app.categories.CategoryRepository
import com.scritch.app.prompt.PromptViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class JamViewModel(
    private val jamRepository: JamRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val mutableViewState = MutableStateFlow(
        JamViewState(
            loadingState = LoadingState.LOADING,
            promptViewState = PromptViewState(
                topic = null,
                medium = null,
                support = null,
                constraint = null,
                selectedOption = null,
            )
        )
    )
    val viewState = mutableViewState.asStateFlow()

    private suspend fun loadJamData() {
        //TODO reset view state. Check if its already loaded and act accordingly
        val jamDto = jamRepository.loadLatestJam()

        if (jamDto == null){
            mutableViewState.update {
                it.copy(
                    loadingState = LoadingState.NO_JAM,
                )
            }
        } else {
            TODO("for each key, load that option data")
            TODO("insert that data into the view state")
            val topicDto = jamDto.topic?.let { topicId ->
                categoryRepository.getOption(
                    category = Category.Topic,
                    optionId = topicId,
                )
            }?.let { topicDto ->

            }
        }
    }
}