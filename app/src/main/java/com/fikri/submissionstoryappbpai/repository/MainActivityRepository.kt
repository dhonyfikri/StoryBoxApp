package com.fikri.submissionstoryappbpai.repository

import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.getDayDiff
import com.fikri.submissionstoryappbpai.other_class.getStringDate
import com.fikri.submissionstoryappbpai.other_class.toDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class MainActivityRepository {
    suspend fun validatingLoginSession(
        pref: DataStorePreferences
    ): Boolean {
        return withContext(Dispatchers.Main) {
            val currentDate = getStringDate().toDate()
            val lastLoginDate =
                pref.getDataStoreValue(DataStorePreferences.SESSION_KEY).first().toDate()
            val token = pref.getDataStoreValue(DataStorePreferences.TOKEN_KEY).first()
            getDayDiff(lastLoginDate, currentDate) < 1 && token.isNotEmpty()
        }
    }

    suspend fun saveCurrentSession(
        pref: DataStorePreferences
    ) {
        withContext(Dispatchers.Main) {
            pref.saveDataStoreValue(DataStorePreferences.SESSION_KEY, getStringDate())
        }
    }
}