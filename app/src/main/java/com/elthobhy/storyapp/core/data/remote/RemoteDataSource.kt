package com.elthobhy.storyapp.core.data.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.elthobhy.storyapp.core.data.remote.model.request.LoginRequest
import com.elthobhy.storyapp.core.data.remote.model.request.RegisterRequest
import com.elthobhy.storyapp.core.data.remote.model.response.BaseResponse
import com.elthobhy.storyapp.core.data.remote.model.response.ListStoryItem
import com.elthobhy.storyapp.core.data.remote.model.response.LoginResponse
import com.elthobhy.storyapp.core.data.remote.model.response.vo.ApiResponse
import com.elthobhy.storyapp.core.data.remote.network.ApiConfig
import com.elthobhy.storyapp.core.utils.UserPreferences
import com.elthobhy.storyapp.core.utils.vo.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteDataSource(private val pref: UserPreferences) {

    fun login(email: String, passwd: String): LiveData<Resource<String>> {
        val auth = MutableLiveData<Resource<String>>()
        auth.postValue(Resource.loading())
        val client = ApiConfig.getApiService().login(LoginRequest(email = email, password = passwd))

        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()?.loginResult?.token
                    auth.postValue(Resource.success(result))
                } else {
                    val error = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        BaseResponse::class.java
                    )
                    auth.postValue(Resource.success(error.message))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("error", "onFailure: ${t.message}")
                auth.postValue(Resource.success(t.message))
            }
        })
        return auth
    }

    fun register(name: String, email: String, passwd: String): LiveData<Resource<String>> {
        val register = MutableLiveData<Resource<String>>()
        register.postValue(Resource.loading())
        val client =
            ApiConfig.getApiService()
                .register(RegisterRequest(password = passwd, name = name, email = email))

        client.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful) {
                    val message = response.body()?.message.toString()
                    register.postValue(Resource.success(message))
                } else {
                    val error = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        BaseResponse::class.java
                    )
                    register.postValue(Resource.success(error.message))
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                register.postValue(Resource.success(t.message))
            }

        })
        return register
    }

    suspend fun getStories(): Flow<ApiResponse<List<ListStoryItem>>> {
        return flow {
            try {
                val response = ApiConfig.getApiService().getStories(
                    token = "Bearer ${pref.getUserToken().first()}"
                )
                val listStory = response.listStory
                emit(ApiResponse.success(listStory))
            }catch (e: Exception) {
                Log.d("remoteData", "getStories: ${e.message}")
                emit(ApiResponse.error(e.message.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun postStory(
        imageMultipart: MultipartBody.Part,
        description: RequestBody,
    ): LiveData<Resource<String>> {
        val post = MutableLiveData<Resource<String>>()
        post.postValue(Resource.loading())
        val client = ApiConfig.getApiService().addStory(
            token = "Bearer ${pref.getUserToken().first()}",
            file = imageMultipart,
            description = description
        )
        client.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful) {
                    post.postValue(Resource.success(response.body()?.message))
                } else {
                    val error = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        BaseResponse::class.java
                    )
                    post.postValue(Resource.error(error.message))
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                post.postValue(Resource.error(t.message))
            }
        })
        return post
    }

    suspend fun saveUserKey(token: String) {
        pref.saveUserToken(token)
    }

}