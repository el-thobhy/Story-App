package com.elthobhy.storyapp.core.domain.usecase

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.elthobhy.storyapp.core.data.remote.model.response.BaseResponse
import com.elthobhy.storyapp.core.domain.model.Story
import com.elthobhy.storyapp.core.domain.repository.RepositoryInterface
import com.elthobhy.storyapp.core.utils.vo.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryInteractionRepository(private val repository: RepositoryInterface) : StoryUsecase {
    override fun getStoriesLocation(): Flow<Resource<List<Story>>> = repository.getStoriesLocation()
    override fun getStories(): Flow<Resource<PagingData<Story>>> = repository.getStories()
    override suspend fun getDataLogin(email: String, passwd: String): LiveData<Resource<String>> =
        repository.getDataLogin(email, passwd)

    override suspend fun saveToken(token: String): String = repository.saveToken(token)
    override suspend fun getDataRegister(
        name: String,
        email: String,
        passwd: String
    ): LiveData<Resource<String>> {
        return repository.getDataRegister(name, email, passwd)
    }

    override suspend fun postStory(
        imageMultipart: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): LiveData<Resource<BaseResponse>> {
        return repository.postStory(imageMultipart, description, lat, lon)
    }

    override suspend fun delete() {
        repository.delete()
    }
}