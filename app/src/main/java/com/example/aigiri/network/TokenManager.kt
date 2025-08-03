package com.example.aigiri.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("auth_pref")
class TokenManager(context: Context) {

    companion object {
        private val JWT_TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }


    private val dataStore = context.applicationContext.dataStore
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var cachedToken: String? = null

    @Volatile
    private var cachedUserId: String? = null

    val tokenFlow: Flow<String?> = dataStore.data
        .map { preferences -> preferences[JWT_TOKEN_KEY] }

    val userIdFlow: Flow<String?> = dataStore.data
        .map { preferences -> preferences[USER_ID_KEY] }

    init {
        scope.launch {
            tokenFlow.collect { cachedToken = it }
        }
        scope.launch {
            userIdFlow.collect { cachedUserId = it }
        }
    }

    suspend fun saveTokenAndUserId(token: String, userId: String) {
        dataStore.edit { preferences ->
            preferences[JWT_TOKEN_KEY] = token
            preferences[USER_ID_KEY] = userId
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(JWT_TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
        }
    }

    fun getCachedToken(): String? = cachedToken
    fun getCachedUserId(): String? = cachedUserId
}
