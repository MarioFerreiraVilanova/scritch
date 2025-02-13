package com.scritch.app.home

import androidx.lifecycle.ViewModel
import com.scritch.app.categories.LoadUserOptionsUseCase

class HomeViewModel(
    private val loadUserOptions: LoadUserOptionsUseCase,
) : ViewModel() {

    fun onGeneratePrompt() {
        TODO("Not yet implemented")
    }
}