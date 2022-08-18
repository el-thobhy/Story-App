package com.elthobhy.storyapp.core.data

import androidx.lifecycle.LiveData
import com.elthobhy.storyapp.core.data.remote.RemoteDataSource
import com.elthobhy.storyapp.core.utils.Resource

class Repository(private val remoteDataSource: RemoteDataSource): RepositoryInterface {
    override fun getDataLogin(email:String, passwd:String): LiveData<Resource<String>> {
        return remoteDataSource.login(email, passwd)
    }

    override suspend fun getToken(token: String){
        return remoteDataSource.saveUserKey(token)
    }

    override fun getDataRegister(
        name: String,
        email: String,
        passwd: String
    ): LiveData<Resource<String>> {
        return remoteDataSource.register(name = name, email = email, passwd = passwd)
    }
}