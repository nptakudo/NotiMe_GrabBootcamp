package com.example.frontend.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingDataSource @Inject constructor(@ApplicationContext private val context: Context) {
    private val accessKey = stringPreferencesKey("access")
    private val refreshKey = stringPreferencesKey("refresh")
    private val username = stringPreferencesKey("username")
    private val userId = stringPreferencesKey("userId")

    fun getAccessToken(): Flow<String> {
        return context.dataStore.data.map {
            it[accessKey] ?: ""
        }
    }

    fun getRefreshToken(): Flow<String> {
        return context.dataStore.data.map {
            it[refreshKey] ?: ""
        }
    }

    suspend fun setAccessToken(token: String) {
        context.dataStore.edit {
            it[accessKey] = token
        }
    }

    suspend fun setRefreshToken(token: String) {
        context.dataStore.edit {
            it[refreshKey] = token
        }
    }

    suspend fun clearAllTokens() {
        context.dataStore.edit {
            it.remove(accessKey)
            it.remove(refreshKey)
        }
    }

    suspend fun setUsername(username: String) {
        context.dataStore.edit {
            it[this.username] = username
        }
    }

    fun getUsername(): Flow<String> {
        return context.dataStore.data.map {
            it[username] ?: ""
        }
    }

    suspend fun setUserId(id: Int) {
        context.dataStore.edit {
            it[this.userId] = id.toString()
        }
    }

    fun getUserId(): Flow<Int> {
        return context.dataStore.data.map {
            it[userId]?.toInt() ?: 0
        }
    }
}