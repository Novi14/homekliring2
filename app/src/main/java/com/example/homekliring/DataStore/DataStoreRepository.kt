package com.example.homekliring.DataStore


import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.homekliring.DataStore.DataStoreRepository.PreferenceKeys.ACCESS_TOKEN_PREFERENCE_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

const val PREFERENCE_NAME = "app_preferences"

class DataStoreRepository (private val dataStore: DataStore<Preferences>) {
    private object PreferenceKeys {

        val ACCESS_TOKEN_PREFERENCE_KEY = stringPreferencesKey("token_preference_key")
        val tokenPreferenceKey = stringPreferencesKey("token_preference_key")
        val loginStatePreferenceKey = booleanPreferencesKey("login_state_preference_key")
    }


    // since saving data with data-store usually running in suspend or coroutine scope,
    suspend fun saveTokenToDataStore(token: String) {
        dataStore.edit { mutablePreferences ->
            // data-store format [name] = value
            mutablePreferences[PreferenceKeys.tokenPreferenceKey] = token
        }
    }

    val readTokenFromDataStore: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { value ->
            val tokenUser = value[PreferenceKeys.tokenPreferenceKey] ?: "none"
            tokenUser
        }

    // since saving data with data-store usually running in suspend or coroutine scope,
    // since this will always run after successfully login
    // i'm just going to put 'true' boolean as a default parameter.
    suspend fun saveLoginToDataStore(state: Boolean = true) {
        dataStore.edit { mutablePreferences ->
            // data-store format [name] = value
            mutablePreferences[PreferenceKeys.loginStatePreferenceKey] = state
        }
    }

    val readLoginStateFromDataStore: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { value ->
            val tokenUser = value[PreferenceKeys.loginStatePreferenceKey] ?: false
            tokenUser
        }

    //Read login access token
    val accessToken: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d("PreferencesDataStore", exception.message.toString())

                emit(emptyPreferences())
            } else {
                throw exception
            }
        }

        .map { preferences ->
            preferences[ACCESS_TOKEN_PREFERENCE_KEY] ?: ""
        }

}