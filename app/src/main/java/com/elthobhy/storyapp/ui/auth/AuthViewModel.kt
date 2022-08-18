package com.elthobhy.storyapp.ui.auth

import android.util.Log
import androidx.lifecycle.*
import com.elthobhy.storyapp.core.data.Repository
import com.elthobhy.storyapp.core.data.remote.ApiService
import com.elthobhy.storyapp.core.data.remote.model.request.LoginRequest
import com.elthobhy.storyapp.core.data.remote.model.response.BaseResponse
import com.elthobhy.storyapp.core.data.remote.model.response.LoginResponse
import com.elthobhy.storyapp.core.utils.Resource
import com.elthobhy.storyapp.core.utils.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(private val pref: UserPreferences, private val repository: Repository): ViewModel(){


    fun login(email:String, passwd:String) = repository.getData(email, passwd)

    fun logout() {
        val auth = MutableLiveData<Resource<String>>()
        viewModelScope.launch{
            pref.deleteUser()
            auth.postValue(Resource.Success(null))
        }
    }
    suspend fun saveKey(token: String){
        repository.getToken(token)
    }

    fun getToken() = pref.getUserToken().asLiveData()
}