package com.fikri.submissionstoryappbpai.di

import android.content.Context
import com.fikri.submissionstoryappbpai.api.ApiConfig
import com.fikri.submissionstoryappbpai.database.AppDatabase
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.dataStore
import com.fikri.submissionstoryappbpai.repository.BasicStoryRepository

object Injection {
    fun provideRepository(context: Context): BasicStoryRepository {
        val pref = DataStorePreferences.getInstance(context.dataStore)
        val database = AppDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return BasicStoryRepository(pref, database, apiService)
    }
}