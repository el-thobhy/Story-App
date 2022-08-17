package com.elthobhy.storyapp.ui.auth

import android.util.Log
import androidx.lifecycle.*
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

class AuthViewModel(private val pref: UserPreferences, private val apiService: ApiService): ViewModel(){

    private val _auth = MutableLiveData<Resource<String>>()

    val auth: LiveData<Resource<String>> = _auth

    fun login(email: String, passwd: String){
        _auth.postValue(Resource.Loading())
        val client = apiService.login(LoginRequest(email = email, password = passwd))

        client.enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful){
                    val result = response.body()?.loginResult?.token
                    result?.let { saveUserKey(it) }
                    _auth.postValue(Resource.Success(result))
                }else{
                    val error = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        BaseResponse::class.java
                    )
                    _auth.postValue(Resource.Error(error.message))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("error", "onFailure: ${t.message}" )
                _auth.postValue(Resource.Error(t.message))
            }
        })
    }

    fun logout() = deleteUser()

    private fun deleteUser() {
        viewModelScope.launch {
            pref.deleteUser()
        }
    }

    private fun saveUserKey(token: String) {
        viewModelScope.launch {
            pref.saveUserToken(token)
        }
    }

    fun getToken() = pref.getUserToken().asLiveData()
}