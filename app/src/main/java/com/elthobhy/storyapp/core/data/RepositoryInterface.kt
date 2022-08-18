package com.elthobhy.storyapp.core.data

import androidx.lifecycle.LiveData
import com.elthobhy.storyapp.core.data.remote.model.response.ListStoryItem
import com.elthobhy.storyapp.core.utils.Resource

interface RepositoryInterface {
    fun getDataLogin(email: String, passwd: String): LiveData<Resource<String>>
    suspend fun getToken(token:String)
    fun getDataRegister(name: String, email: String, passwd: String): LiveData<Resource<String>>
    suspend fun getStories(): LiveData<Resource<ArrayList<ListStoryItem>>>
}