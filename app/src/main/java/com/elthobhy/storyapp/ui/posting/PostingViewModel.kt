package com.elthobhy.storyapp.ui.posting

import androidx.lifecycle.ViewModel
import com.elthobhy.storyapp.core.domain.usecase.StoryUsecase
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostingViewModel(private val useCase: StoryUsecase) : ViewModel() {
    suspend fun postingStory(
        imageMultipart: MultipartBody.Part,
        description: RequestBody
    ) = useCase.postStory(imageMultipart, description)
}