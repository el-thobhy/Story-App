package com.elthobhy.storyapp.core.data.remote

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.elthobhy.storyapp.core.data.remote.model.request.LoginRequest
import com.elthobhy.storyapp.core.data.remote.model.response.BaseResponse
import com.elthobhy.storyapp.core.data.remote.model.response.LoginResponse
import com.elthobhy.storyapp.core.utils.Resource
import com.elthobhy.storyapp.core.utils.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoteDataSource(private val pref: UserPreferences,private val apiService: ApiService) {
    fun login(email: String, passwd: String): LiveData<Resource<String>> {
        val auth = MutableLiveData<Resource<String>>()
        auth.postValue(Resource.Loading())
        val client = apiService.login(LoginRequest(email = email, password = passwd))

        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful){
                    val result = response.body()?.loginResult?.token
                    auth.postValue(Resource.Success(result))
                }else{
                    val error = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        BaseResponse::class.java
                    )
                    auth.postValue(Resource.Error(error.message))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("error", "onFailure: ${t.message}" )
                auth.postValue(Resource.Error(t.message))
            }
        })
        return auth
    }

    suspend fun saveUserKey(token: String) {
            pref.saveUserToken(token)
    }

}