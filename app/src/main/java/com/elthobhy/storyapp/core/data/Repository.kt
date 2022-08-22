package com.elthobhy.storyapp.core.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.elthobhy.storyapp.core.data.local.LocalDataSource
import com.elthobhy.storyapp.core.data.local.room.StoryDatabase
import com.elthobhy.storyapp.core.data.remote.RemoteDataSource
import com.elthobhy.storyapp.core.data.remote.model.response.BaseResponse
import com.elthobhy.storyapp.core.data.remote.model.response.ListStoryItem
import com.elthobhy.storyapp.core.data.remote.model.response.vo.ApiResponse
import com.elthobhy.storyapp.core.domain.model.Story
import com.elthobhy.storyapp.core.domain.repository.RepositoryInterface
import com.elthobhy.storyapp.core.utils.DataMapper
import com.elthobhy.storyapp.core.utils.UserPreferences
import com.elthobhy.storyapp.core.utils.vo.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository(
    private val database: StoryDatabase,
    private val preferences: UserPreferences,
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
    ) : RepositoryInterface {
    override fun getDataLogin(email: String, passwd: String): LiveData<Resource<String>> {
        return remoteDataSource.login(email, passwd)
    }

    override suspend fun saveToken(token: String): String = remoteDataSource.saveUserKey(token)


    override fun getDataRegister(
        name: String,
        email: String,
        passwd: String
    ): LiveData<Resource<String>> {
        return remoteDataSource.register(name = name, email = email, passwd = passwd)
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getStories(): Flow<Resource<PagingData<Story>>> =
        object : NetworkBoundResource<PagingData<Story>, List<ListStoryItem>>(){
            override suspend fun loadFromDb(): Flow<PagingData<Story>> {
                return Pager(
                    config = PagingConfig(
                        pageSize = 5
                    ),
                    remoteMediator = StoryRemoteMediator(database, preferences),
                    pagingSourceFactory = {
                        localDataSource.getStories()
                    }
                ).flow.map { DataMapper.mapPagingEntityToDomain(it) }

            }

            override fun shouldFetch(data: PagingData<Story>?): Boolean {
                return data == null
            }

            override suspend fun createCall(): Flow<ApiResponse<List<ListStoryItem>>> {
                return remoteDataSource.getStories()
            }

            override suspend fun saveCallResult(data: List<ListStoryItem>) {
                val dataMap = DataMapper.mapResponseToEntity(data)
                return localDataSource.insertStories(dataMap)
            }
        }.asFlow()

    override fun getStoriesLocation(): Flow<Resource<List<Story>>> =
        object : NetworkBoundResource<List<Story>, List<ListStoryItem>>(){
            override suspend fun loadFromDb(): Flow<List<Story>> {
                return localDataSource.getStoriesLocation().map { DataMapper.mapEntityToDomain(it) }

            }

            override fun shouldFetch(data: List<Story>?): Boolean {
                return data == null
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
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): LiveData<Resource<BaseResponse>> {
        return remoteDataSource.postStory(
            imageMultipart = imageMultipart,
            description = description,
            lat = lat,
            lon = lon
        )
    }
}