package com.fikri.submissionstoryappbpai.repository

import com.fikri.submissionstoryappbpai.application.MyApplication
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeBottomNavRepository {
    fun clearDataStore(pref: DataStorePreferences) {
        MyApplication().applicationScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                pref.clearDataStore()
            }
        }
    }
}