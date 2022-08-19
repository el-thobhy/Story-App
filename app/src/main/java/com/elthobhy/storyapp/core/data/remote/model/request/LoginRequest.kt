package com.elthobhy.storyapp.core.data.remote.model.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(

    @field:SerializedName("password")
    val password: String? = null,

    @field:SerializedName("email")
    val email: String? = null
)
