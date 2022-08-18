package com.elthobhy.storyapp.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.elthobhy.storyapp.BuildConfig
import com.elthobhy.storyapp.core.data.Repository
import com.elthobhy.storyapp.core.data.RepositoryInterface
import com.elthobhy.storyapp.core.data.remote.ApiService
import com.elthobhy.storyapp.core.data.remote.RemoteDataSource
import com.elthobhy.storyapp.core.utils.UserPreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networking = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(ApiService::class.java)
    }
}
private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "token")
val preferences = module {
    single { UserPreferences.getInstance(androidContext().datastore) }
}

val repository = module {
    single { RemoteDataSource(get(),get()) }
    single { Repository(get()) }
}