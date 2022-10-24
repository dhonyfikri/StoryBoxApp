package com.fikri.submissionstoryappbpai.repository

import android.content.Context
import com.fikri.submissionstoryappbpai.application.MyApplication
import com.fikri.submissionstoryappbpai.database.AppDatabase
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeBottomNavRepository(
    private val application: MyApplication,
    context: Context,
    private val database: AppDatabase
) {
    val pref = DataStorePreferences.getInstance(context.dataStore)
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