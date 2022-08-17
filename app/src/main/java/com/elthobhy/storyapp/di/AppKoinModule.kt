package com.elthobhy.storyapp.di

import com.elthobhy.storyapp.ui.auth.AuthViewModel
import org.koin.dsl.module

val viewModel = module {
    single { AuthViewModel(get(),get()) }
}