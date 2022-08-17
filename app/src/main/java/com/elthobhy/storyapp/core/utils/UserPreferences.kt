package com.elthobhy.storyapp.core.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val dataStore: DataStore<Preferences>){

    private val token = stringPreferencesKey("token")

    fun getUserToken(): Flow<String> {
        return dataStore.data.map { pref->
            pref[token] ?: ""
        }
    }

    suspend fun saveUserToken(userToken: String){
        dataStore.edit { pref->
            pref[token] = userToken
        }
    }

    suspend fun deleteUser(){
        dataStore.edit { pref->
            pref.remove(token)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}