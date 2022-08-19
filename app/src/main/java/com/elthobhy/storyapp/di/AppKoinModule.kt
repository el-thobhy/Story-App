package com.elthobhy.storyapp.di

import com.elthobhy.storyapp.ui.auth.AuthViewModel
import com.elthobhy.storyapp.ui.main.MainViewModel
import com.elthobhy.storyapp.ui.posting.PostingViewModel
import org.koin.dsl.module

val viewModel = module {
    single { AuthViewModel(get(), get()) }
    single { MainViewModel(get()) }
    single { PostingViewModel(get()) }
}