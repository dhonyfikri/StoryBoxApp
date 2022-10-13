package com.fikri.submissionstoryappbpai.other_class

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStorePreferences private constructor(private val dataStore: DataStore<Preferences>) {
    companion object {
        val SESSION_KEY = stringPreferencesKey("login_session")
        val TOKEN_KEY = stringPreferencesKey("token")
        val USER_ID_KEY = stringPreferencesKey("user_id")
        val NAME_KEY = stringPreferencesKey("name")

        @Volatile
        private var INSTANCE: DataStorePreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): DataStorePreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = DataStorePreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    @JvmName("getDataStoreValue1")
    fun getDataStoreValue(keyParamsString: Preferences.Key<String>): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[keyParamsString]
                ?: if (keyParamsString == SESSION_KEY) {
                    "0001-01-01 00:00:00.00"
                } else {
                    ""
                }
        }
    }

    suspend fun saveDataStoreValue(keyParamsString: Preferences.Key<String>, value: String) {
        dataStore.edit { preferences ->
            preferences[keyParamsString] = value
        }
    }

    suspend fun clearDataStore() {
        dataStore.edit { preferences ->
            preferences.remove(SESSION_KEY)
            preferences.remove(TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(NAME_KEY)
        }
    }
}