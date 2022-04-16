package com.willyishmael.dicodingstoryapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>){

    fun getLoginState(): Flow<Boolean> {
        return dataStore.data.map { pref ->
            pref[LOGIN_STATE_KEY] ?: false
        }
    }

    suspend fun setLoginState(state: Boolean) {
        dataStore.edit { pref ->
            pref[LOGIN_STATE_KEY] = state
        }
    }

    fun getCurrentUserToken(): Flow<String> {
        return dataStore.data.map { pref ->
            pref[USER_TOKEN_KEY].toString()
        }
    }

    suspend fun saveUserToken(token: String) {
        dataStore.edit { pref ->
            pref[USER_TOKEN_KEY] = token
        }
    }

    fun getCurrentUserName(): Flow<String> {
        return dataStore.data.map { pref ->
            pref[USER_NAME_KEY].toString()
        }
    }

    suspend fun saveUserName(name: String) {
        dataStore.edit { pref ->
            pref[USER_NAME_KEY]
        }
    }

    companion object {
        private val LOGIN_STATE_KEY = booleanPreferencesKey("login_state")
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")

        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}