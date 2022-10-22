package com.fikri.submissionstoryappbpai.di

import android.content.Context
import com.fikri.submissionstoryappbpai.api.ApiConfig
import com.fikri.submissionstoryappbpai.database.AppDatabase
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.dataStore
import com.fikri.submissionstoryappbpai.repository.MapsStoryRepository
import com.fikri.submissionstoryappbpai.repository.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = DataStorePreferences.getInstance(context.dataStore)
        val database = AppDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(pref, database, apiService)
    }

    fun provideMapsRepository(context: Context): MapsStoryRepository {
        val pref = DataStorePreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return MapsStoryRepository(context, pref, apiService)
    }
}