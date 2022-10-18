package com.fikri.submissionstoryappbpai.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import kotlinx.coroutines.launch

class DisplayConfigurationViewModel(private val pref: DataStorePreferences) : ViewModel() {
    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getDataStoreValue(DataStorePreferences.DARK_MODE_KEY).asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveDataStoreValue(DataStorePreferences.DARK_MODE_KEY, isDarkModeActive)
        }
    }
}