package com.elthobhy.storyapp.core.domain.repository

import androidx.lifecycle.LiveData
import com.elthobhy.storyapp.core.data.local.entity.StoryEntity
import com.elthobhy.storyapp.core.domain.model.Story
import com.elthobhy.storyapp.core.utils.vo.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface RepositoryInterface {
    fun getDataLogin(email: String, passwd: String): LiveData<Resource<String>>
    suspend fun getToken(token: String)
    fun getDataRegister(name: String, email: String, passwd: String): LiveData<Resource<String>>
    fun getStories(): Flow<Resource<List<Story>>>
    suspend fun postStory(
        imageMultipart: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Resource<String>>
}