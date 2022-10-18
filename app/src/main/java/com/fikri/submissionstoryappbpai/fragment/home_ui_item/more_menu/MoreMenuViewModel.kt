package com.fikri.submissionstoryappbpai.fragment.home_ui_item.more_menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences

class MoreMenuViewModel(private val pref: DataStorePreferences) : ViewModel() {
    fun getUserName(): LiveData<String> {
        return pref.getDataStoreValue(DataStorePreferences.NAME_KEY).asLiveData()
    }

    fun getActualLastLogin(): LiveData<String> {
        return pref.getDataStoreValue(DataStorePreferences.LAST_LOGIN_KEY).asLiveData()
    }
}