package com.elthobhy.storyapp.core.data.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.elthobhy.storyapp.core.data.remote.model.request.LoginRequest
import com.elthobhy.storyapp.core.data.remote.model.request.RegisterRequest
import com.elthobhy.storyapp.core.data.remote.model.response.AllStoriesResponse
import com.elthobhy.storyapp.core.data.remote.model.response.BaseResponse
import com.elthobhy.storyapp.core.data.remote.model.response.ListStoryItem
import com.elthobhy.storyapp.core.data.remote.model.response.LoginResponse
import com.elthobhy.storyapp.core.utils.Resource
import com.elthobhy.storyapp.core.utils.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteDataSource(private val pref: UserPreferences, private val apiService: ApiService) {

    fun login(email: String, passwd: String): LiveData<Resource<String>> {
        val auth = MutableLiveData<Resource<String>>()
        auth.postValue(Resource.Loading())
        val client = apiService.login(LoginRequest(email = email, password = passwd))

        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()?.loginResult?.token
                    auth.postValue(Resource.Success(result))
                } else {
                    val error = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        BaseResponse::class.java
                    )
                    auth.postValue(Resource.Error(error.message))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("error", "onFailure: ${t.message}")
                auth.postValue(Resource.Error(t.message))
            }
        })
        return auth
    }

    fun register(name: String, email: String, passwd: String): LiveData<Resource<String>> {
        val register = MutableLiveData<Resource<String>>()
        register.postValue(Resource.Loading())
        val client =
            apiService.register(RegisterRequest(password = passwd, name = name, email = email))

        client.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful) {
                    val message = response.body()?.message.toString()
                    register.postValue(Resource.Success(message))
                } else {
                    val error = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        BaseResponse::class.java
                    )
                    register.postValue(Resource.Error(error.message))
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                register.postValue(Resource.Error(t.message))
            }

        })
        return register
    }

    suspend fun getStories(): LiveData<Resource<ArrayList<ListStoryItem>>> {
        val stories = MutableLiveData<Resource<ArrayList<ListStoryItem>>>()
        stories.postValue(Resource.Loading())
        val client = apiService.getStories(token = "Bearer ${pref.getUserToken().first()}")

        client.enqueue(object : Callback<AllStoriesResponse> {
            override fun onResponse(
                call: Call<AllStoriesResponse>,
                response: Response<AllStoriesResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val list = it.listStory
                        stories.postValue(Resource.Success(list?.let { it1 -> ArrayList(it1) }))
                    }
                } else {
                    val error = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        BaseResponse::class.java
                    )
                    stories.postValue(Resource.Error(error.message))
                }
            }

            override fun onFailure(call: Call<AllStoriesResponse>, t: Throwable) {
                stories.postValue(Resource.Error(t.message))
            }

        })
        return stories
    }

    suspend fun postStory(
        imageMultipart: MultipartBody.Part,
        description: RequestBody,
    ): LiveData<Resource<String>> {
        val post = MutableLiveData<Resource<String>>()
        post.postValue(Resource.Loading())
        val client = apiService.addStory(
            token = "Bearer ${pref.getUserToken().first()}",
            file = imageMultipart,
            description = description
        )
        client.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful) {
                    post.postValue(Resource.Success(response.body()?.message))
                } else {
                    val error = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        BaseResponse::class.java
                    )
                    post.postValue(Resource.Error(error.message))
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                post.postValue(Resource.Error(t.message))
            }
        })
        return post
    }

    suspend fun saveUserKey(token: String) {
        pref.saveUserToken(token)
    }

}