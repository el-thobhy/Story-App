package com.elthobhy.storyapp.core.data

import androidx.lifecycle.LiveData
import com.elthobhy.storyapp.core.utils.Resource

interface RepositoryInterface {
    fun getData(email: String, passwd: String): LiveData<Resource<String>>
    suspend fun getToken(token:String)
}