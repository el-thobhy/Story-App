package com.elthobhy.storyapp.core.domain.usecase

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.elthobhy.storyapp.core.domain.model.Story
import com.elthobhy.storyapp.core.domain.repository.RepositoryInterface
import com.elthobhy.storyapp.core.utils.vo.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryInteraction(private val repository: RepositoryInterface): StoryUsecase{
    override fun getStoriesLocation(): Flow<Resource<List<Story>>> = repository.getStoriesLocation()

    override fun getStories(): Flow<Resource<PagingData<Story>>> =  repository.getStories()
    override fun getDataLogin(email: String, passwd: String): LiveData<Resource<String>> = repository.getDataLogin(email, passwd)
    override suspend fun getToken(token: String) = repository.getToken(token)

    override fun getDataRegister(
        name: String,
        email: String,
        passwd: String
    ): LiveData<Resource<String>> {
        return repository.getDataRegister(name, email, passwd)
    }

    override suspend fun postStory(
        imageMultipart: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Resource<String>> {
        return repository.postStory(imageMultipart, description)
    }
}