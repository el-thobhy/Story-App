package com.elthobhy.storyapp.core.data.remote

import com.elthobhy.storyapp.core.data.remote.model.request.LoginRequest
import com.elthobhy.storyapp.core.data.remote.model.request.RegisterRequest
import com.elthobhy.storyapp.core.data.remote.model.response.AllStoriesResponse
import com.elthobhy.storyapp.core.data.remote.model.response.BaseResponse
import com.elthobhy.storyapp.core.data.remote.model.response.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun register(@Body request: RegisterRequest): Call<BaseResponse>

    @GET("stories")
    fun getStories(
        @Header("Authorization")token: String,
        @Query("page")page: Int? = null,
        @Query("size")size: Int? = null
    ): Call<AllStoriesResponse>
}