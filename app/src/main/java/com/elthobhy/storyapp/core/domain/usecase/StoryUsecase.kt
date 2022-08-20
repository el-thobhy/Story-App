package com.elthobhy.storyapp.core.domain.usecase

import androidx.lifecycle.LiveData
import com.elthobhy.storyapp.core.domain.model.Story
import com.elthobhy.storyapp.core.utils.vo.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface StoryUsecase {
    fun getStories(): Flow<Resource<List<Story>>>
    fun getDataLogin(email: String, passwd: String): LiveData<Resource<String>>
    suspend fun getToken(token: String)
    fun getDataRegister(name: String, email: String, passwd: String): LiveData<Resource<String>>
    suspend fun postStory(
        imageMultipart: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Resource<String>>
}