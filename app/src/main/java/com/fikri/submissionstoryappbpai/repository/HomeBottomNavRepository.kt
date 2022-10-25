package com.fikri.submissionstoryappbpai.repository

import com.fikri.submissionstoryappbpai.application.MyApplication
import com.fikri.submissionstoryappbpai.database.AppDatabase
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeBottomNavRepository(
    private val application: MyApplication,
    private val pref: DataStorePreferences,
    private val database: AppDatabase
) {
    fun clearDataStore() {
        application.applicationScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                pref.clearDataStore()
                database.remoteKeysDao().deleteRemoteKeys()
                database.storyDao().deleteAllBasicStory()
            }
        }
    }
}