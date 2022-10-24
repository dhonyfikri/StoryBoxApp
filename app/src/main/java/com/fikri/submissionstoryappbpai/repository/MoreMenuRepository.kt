package com.fikri.submissionstoryappbpai.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.dataStore

class MoreMenuRepository(context: Context) {
    val pref = DataStorePreferences.getInstance(context.dataStore)

    fun getUserName(): LiveData<String> {
        return pref.getDataStoreValue(DataStorePreferences.NAME_KEY).asLiveData()
    }

    fun getActualLastLogin(): LiveData<String> {
        return pref.getDataStoreValue(DataStorePreferences.LAST_LOGIN_KEY).asLiveData()
    }
}