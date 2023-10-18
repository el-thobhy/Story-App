package com.elthobhy.storyapp.ui.auth.register

import androidx.lifecycle.ViewModel
import com.elthobhy.storyapp.core.domain.usecase.StoryUsecase

class RegisterViewModel(private val useCase: StoryUsecase) : ViewModel() {
    suspend fun register(name: String, email: String, passwd: String) =
        useCase.getDataRegister(name = name, email = email, passwd = passwd)
}