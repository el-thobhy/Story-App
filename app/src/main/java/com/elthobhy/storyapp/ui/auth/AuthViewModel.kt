package com.elthobhy.storyapp.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.elthobhy.storyapp.core.domain.usecase.StoryUsecase
import com.elthobhy.storyapp.core.utils.UserPreferences
import com.elthobhy.storyapp.core.utils.vo.Resource
import kotlinx.coroutines.launch

class AuthViewModel(private val pref: UserPreferences, private val useCase: StoryUsecase) :
    ViewModel() {

    fun logout(): MutableLiveData<Resource<String>> {
        val auth = MutableLiveData<Resource<String>>()
        viewModelScope.launch {
            if (pref.deleteUser().isEmpty()) auth.postValue(Resource.success(null))
            else auth.postValue(Resource.error("Error"))
        }
        return auth
    }

    suspend fun saveKey(token: String): String =
        useCase.saveToken(token)


    fun getToken() = pref.getUserToken().asLiveData()
}