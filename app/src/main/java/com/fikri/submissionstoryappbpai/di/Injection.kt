package com.fikri.submissionstoryappbpai.di

import android.content.Context
import com.fikri.submissionstoryappbpai.api.ApiConfig
import com.fikri.submissionstoryappbpai.application.MyApplication
import com.fikri.submissionstoryappbpai.database.AppDatabase
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.dataStore
import com.fikri.submissionstoryappbpai.repository.*

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = DataStorePreferences.getInstance(context.dataStore)
        val database = AppDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(pref, database, apiService)
    }

    fun provideMapsRepository(context: Context): MapsStoryRepository {
        val apiService = ApiConfig.getApiService()
        return MapsStoryRepository(context, apiService)
    }

    fun provideCreateStoryRepository(context: Context): CreateStoryRepository {
        val apiService = ApiConfig.getApiService()
        return CreateStoryRepository(context, apiService)
    }

    fun provideCreateMapStoryRepository(context: Context): CreateStoryMapRepository {
        val apiService = ApiConfig.getApiService()
        return CreateStoryMapRepository(context, apiService)
    }

    fun provideMainActivityRepository(context: Context): MainActivityRepository {
        return MainActivityRepository(context)
    }

    fun provideLoginRepository(context: Context): LoginRepository {
        return LoginRepository(context)
    }

    fun provideRegisterRepository() = RegisterRepository()

    fun provideHomeBottomNavRepository(context: Context): HomeBottomNavRepository {
        val application = MyApplication()
        val database = AppDatabase.getDatabase(context)
        return HomeBottomNavRepository(application, context, database)
    }

    fun provideDisplayConfigurationRepository(context: Context): DisplayConfigurationRepository {
        val application = MyApplication()
        return DisplayConfigurationRepository(application, context)
    }

    fun provideMoreMenuRepository(context: Context): MoreMenuRepository {
        return MoreMenuRepository(context)
    }
}