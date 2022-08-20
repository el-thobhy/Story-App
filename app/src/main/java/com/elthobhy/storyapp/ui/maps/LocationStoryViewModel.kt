package com.elthobhy.storyapp.ui.maps

import androidx.lifecycle.asLiveData
import com.elthobhy.storyapp.core.domain.usecase.StoryUsecase

class LocationStoryViewModel(private val useCase: StoryUsecase) {
    fun getStories() = useCase.getStoriesLocation().asLiveData()
}