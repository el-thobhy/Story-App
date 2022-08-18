package com.elthobhy.storyapp.ui.posting

import androidx.lifecycle.ViewModel
import com.elthobhy.storyapp.core.data.Repository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostingViewModel(private val repository: Repository): ViewModel() {
    suspend fun postingStory(
        imageMultipart: MultipartBody.Part,
        description: RequestBody) = repository.postStory(imageMultipart,description)
}