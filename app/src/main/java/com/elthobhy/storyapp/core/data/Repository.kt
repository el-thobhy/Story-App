package com.elthobhy.storyapp.core.data

import androidx.lifecycle.LiveData
import com.elthobhy.storyapp.core.data.local.LocalDataSource
import com.elthobhy.storyapp.core.data.remote.RemoteDataSource
import com.elthobhy.storyapp.core.data.remote.model.response.ListStoryItem
import com.elthobhy.storyapp.core.data.remote.model.response.vo.ApiResponse
import com.elthobhy.storyapp.core.domain.model.Story
import com.elthobhy.storyapp.core.domain.repository.RepositoryInterface
import com.elthobhy.storyapp.core.utils.DataMapper
import com.elthobhy.storyapp.core.utils.vo.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
    ) : RepositoryInterface {
    override fun getDataLogin(email: String, passwd: String): LiveData<Resource<String>> {
        return remoteDataSource.login(email, passwd)
    }

    override suspend fun getToken(token: String) {
        return remoteDataSource.saveUserKey(token)
    }

    override fun getDataRegister(
        name: String,
        email: String,
        passwd: String
    ): LiveData<Resource<String>> {
        return remoteDataSource.register(name = name, email = email, passwd = passwd)
    }

    override fun getStories(): Flow<Resource<List<Story>>> =
        object : NetworkBoundResource<List<Story>, List<ListStoryItem>>(){
            override suspend fun loadFromDb(): Flow<List<Story>> {
                return localDataSource.getStories().map { DataMapper.mapEntityToDomain(it) }
            }

            override fun shouldFetch(data: List<Story>?): Boolean {
                return true
            }

            override suspend fun createCall(): Flow<ApiResponse<List<ListStoryItem>>> {
                return remoteDataSource.getStories()
            }

            override suspend fun saveCallResult(data: List<ListStoryItem>) {
                val dataMap = DataMapper.mapResponseToEntity(data)
                return localDataSource.insertStories(dataMap)
            }
        }.asFlow()


    override suspend fun postStory(
        imageMultipart: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Resource<String>> {
        return remoteDataSource.postStory(
            imageMultipart = imageMultipart,
            description = description
        )
    }
}