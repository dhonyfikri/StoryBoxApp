package com.fikri.submissionstoryappbpai.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.fikri.submissionstoryappbpai.application.MyApplication
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import kotlinx.coroutines.launch

class DisplayConfigurationRepository(
    private val application: MyApplication,
    private val pref: DataStorePreferences
) {
    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getDataStoreValue(DataStorePreferences.DARK_MODE_KEY).asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        application.applicationScope.launch {
            pref.saveDataStoreValue(DataStorePreferences.DARK_MODE_KEY, isDarkModeActive)
        }
    }

    fun getMapMode(): LiveData<String> {
        return pref.getDataStoreValue(DataStorePreferences.MAP_MODE_KEY).asLiveData()
    }

    fun saveMapMode(type: String) {
        application.applicationScope.launch {
            pref.saveDataStoreValue(DataStorePreferences.MAP_MODE_KEY, type)
        }
    }
}