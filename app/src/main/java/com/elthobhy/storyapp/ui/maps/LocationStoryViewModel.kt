package com.elthobhy.storyapp.ui.maps

import androidx.lifecycle.asLiveData
import com.elthobhy.storyapp.core.domain.usecase.StoryUsecase
import com.elthobhy.storyapp.core.utils.UserPreferences

class LocationStoryViewModel(private val useCase: StoryUsecase) {
    fun getStories() = useCase.getStoriesLocation().asLiveData()
}