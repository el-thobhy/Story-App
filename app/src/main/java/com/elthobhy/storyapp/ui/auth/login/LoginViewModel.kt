package com.elthobhy.storyapp.ui.auth.login

import androidx.lifecycle.ViewModel
import com.elthobhy.storyapp.core.domain.usecase.StoryUsecase

class LoginViewModel(private val useCase: StoryUsecase) : ViewModel() {
    fun login(email: String, passwd: String) = useCase.getDataLogin(email, passwd)
}