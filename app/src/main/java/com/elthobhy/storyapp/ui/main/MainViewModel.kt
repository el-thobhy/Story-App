package com.elthobhy.storyapp.ui.main

import androidx.lifecycle.ViewModel
import com.elthobhy.storyapp.core.data.Repository

class MainViewModel(private val repository: Repository) : ViewModel() {
    suspend fun getStories() = repository.getStories()
}