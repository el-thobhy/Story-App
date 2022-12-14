package com.elthobhy.storyapp

import android.app.Application
import com.elthobhy.storyapp.core.di.*
import com.elthobhy.storyapp.di.useCaseModule
import com.elthobhy.storyapp.di.viewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MyApp)
            modules(
                listOf(
                    networking,
                    viewModel,
                    preferences,
                    repository,
                    adapter,
                    useCaseModule
                )
            )
        }
    }
}