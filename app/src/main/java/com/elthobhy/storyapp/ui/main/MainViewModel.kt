package com.elthobhy.storyapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.elthobhy.storyapp.core.domain.usecase.StoryUsecase

class MainViewModel(private val useCase: StoryUsecase) : ViewModel() {
    fun getStories() = useCase.getStories().asLiveData()
    suspend fun delete() = useCase.delete()
}