package com.elthobhy.storyapp.core.data.remote

import com.elthobhy.storyapp.core.data.remote.model.request.LoginRequest
import com.elthobhy.storyapp.core.data.remote.model.request.RegisterRequest
import com.elthobhy.storyapp.core.data.remote.model.response.BaseResponse
import com.elthobhy.storyapp.core.data.remote.model.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun register(@Body request: RegisterRequest): Call<BaseResponse>
}