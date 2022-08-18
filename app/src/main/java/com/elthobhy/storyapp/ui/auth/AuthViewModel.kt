package com.elthobhy.storyapp.ui.auth

import androidx.lifecycle.*
import com.elthobhy.storyapp.core.data.Repository
import com.elthobhy.storyapp.core.utils.Resource
import com.elthobhy.storyapp.core.utils.UserPreferences
import kotlinx.coroutines.launch

class AuthViewModel(private val pref: UserPreferences, private val repository: Repository): ViewModel(){

    fun login(email:String, passwd:String) = repository.getDataLogin(email, passwd)
    fun register(name: String, email: String, passwd: String) = repository.getDataRegister(name = name, email = email, passwd = passwd)
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