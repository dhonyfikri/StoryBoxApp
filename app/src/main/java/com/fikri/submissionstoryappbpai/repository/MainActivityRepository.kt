package com.fikri.submissionstoryappbpai.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.fikri.submissionstoryappbpai.other_class.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class MainActivityRepository(context: Context) {
    val pref = DataStorePreferences.getInstance(context.dataStore)
    suspend fun validatingLoginSession(): Boolean {
        return withContext(Dispatchers.Main) {
            val currentDate = getStringDate().toDate()
            val lastLoginDate =
                pref.getDataStoreValue(DataStorePreferences.SESSION_KEY).first().toDate()
            val token = pref.getDataStoreValue(DataStorePreferences.TOKEN_KEY).first()
            getDayDiff(lastLoginDate, currentDate) < 1 && token.isNotEmpty()
        }
    }

    suspend fun saveCurrentSession() {
        withContext(Dispatchers.Main) {
            pref.saveDataStoreValue(DataStorePreferences.SESSION_KEY, getStringDate())
        }
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getDataStoreValue(DataStorePreferences.DARK_MODE_KEY).asLiveData()
    }
}