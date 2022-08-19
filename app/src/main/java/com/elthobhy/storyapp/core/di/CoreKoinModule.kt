package com.elthobhy.storyapp.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.elthobhy.storyapp.core.data.Repository
import com.elthobhy.storyapp.core.data.remote.ApiConfig
import com.elthobhy.storyapp.core.data.remote.RemoteDataSource
import com.elthobhy.storyapp.core.ui.StoryAdapter
import com.elthobhy.storyapp.core.utils.UserPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val networking = module {
    single { ApiConfig }
}
private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "token")
val preferences = module {
    single { UserPreferences.getInstance(androidContext().datastore) }
}

val repository = module {
    single { RemoteDataSource(get()) }
    single { Repository(get()) }
}
val adapter = module {
    single { StoryAdapter() }
}