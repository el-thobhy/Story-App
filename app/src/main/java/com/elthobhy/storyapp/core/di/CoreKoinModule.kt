package com.elthobhy.storyapp.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.elthobhy.storyapp.core.data.Repository
import com.elthobhy.storyapp.core.data.local.LocalDataSource
import com.elthobhy.storyapp.core.data.local.room.StoryDatabase
import com.elthobhy.storyapp.core.data.remote.network.ApiConfig
import com.elthobhy.storyapp.core.data.remote.RemoteDataSource
import com.elthobhy.storyapp.core.domain.repository.RepositoryInterface
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
    single { RemoteDataSource(get(),get()) }
    single { LocalDataSource(get()) }
    single<RepositoryInterface> { Repository(get(),get()) }
    single { StoryDatabase.getInstance(get()) }
    factory { get<StoryDatabase>().storyDao() }
}
val adapter = module {
    single { StoryAdapter() }
}