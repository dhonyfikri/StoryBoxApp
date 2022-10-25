package com.fikri.submissionstoryappbpai.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences

class MoreMenuRepository(private val pref: DataStorePreferences) {
    fun getUserName(): LiveData<String> {
        return pref.getDataStoreValue(DataStorePreferences.NAME_KEY).asLiveData()
    }

    fun getActualLastLogin(): LiveData<String> {
        return pref.getDataStoreValue(DataStorePreferences.LAST_LOGIN_KEY).asLiveData()
    }
}