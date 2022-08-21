package com.elthobhy.storyapp.ui.auth

import androidx.lifecycle.*
import com.elthobhy.storyapp.core.data.Repository
import com.elthobhy.storyapp.core.domain.usecase.StoryUsecase
import com.elthobhy.storyapp.core.utils.UserPreferences
import com.elthobhy.storyapp.core.utils.vo.Resource
import kotlinx.coroutines.launch

class AuthViewModel(private val pref: UserPreferences, private val useCase: StoryUsecase) :
    ViewModel() {

    fun logout() {
        val auth = MutableLiveData<Resource<String>>()
        viewModelScope.launch {
            pref.deleteUser()
            auth.postValue(Resource.success(null))
        }
    }

    suspend fun saveKey(token: String) {
        useCase.getToken(token)
    }

    fun getToken() = pref.getUserToken().asLiveData()
}