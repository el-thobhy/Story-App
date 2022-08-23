package com.elthobhy.storyapp.core.data.remote.network


import com.elthobhy.storyapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        var BASE_URL = "http://127.0.0.1:8080/"
        fun getApiService(): ApiService {
            val client = OkHttpClient.Builder()

            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                logging.redactHeader("Authorization")
                logging.redactHeader("Cookie")
                client.addInterceptor(logging)
            }

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}