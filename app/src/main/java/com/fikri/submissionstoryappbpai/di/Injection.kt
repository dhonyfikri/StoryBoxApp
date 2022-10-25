package com.fikri.submissionstoryappbpai.di

import android.content.Context
import android.location.Geocoder
import com.fikri.submissionstoryappbpai.api.ApiConfig
import com.fikri.submissionstoryappbpai.application.MyApplication
import com.fikri.submissionstoryappbpai.database.AppDatabase
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.dataStore
import com.fikri.submissionstoryappbpai.repository.*
import java.util.*

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = DataStorePreferences.getInstance(context.dataStore)
        val database = AppDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(pref, database, apiService)
    }

    fun provideMapsRepository(context: Context): MapsStoryRepository {
        val application = MyApplication()
        val resources = context.resources
        val pref = DataStorePreferences.getInstance(context.dataStore)
        val geocoder = Geocoder(context, Locale.getDefault())
        val apiService = ApiConfig.getApiService()
        return MapsStoryRepository(application, resources, pref, geocoder, apiService)
    }

    fun provideCreateStoryRepository(context: Context): CreateStoryRepository {
        val application = MyApplication()
        val resources = context.resources
        val pref = DataStorePreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return CreateStoryRepository(application, resources, pref, apiService)
    }

    fun provideCreateMapStoryRepository(context: Context): CreateStoryMapRepository {
        val application = MyApplication()
        val resources = context.resources
        val pref = DataStorePreferences.getInstance(context.dataStore)
        val geocoder = Geocoder(context, Locale.getDefault())
        val apiService = ApiConfig.getApiService()
        return CreateStoryMapRepository(application, resources, pref, geocoder, apiService)
    }

    fun provideMainActivityRepository(context: Context): MainActivityRepository {
        val pref = DataStorePreferences.getInstance(context.dataStore)
        return MainActivityRepository(pref)
    }

    fun provideLoginRepository(context: Context): LoginRepository {
        val resources = context.resources
        val pref = DataStorePreferences.getInstance(context.dataStore)
        return LoginRepository(resources, pref)
    }

    fun provideRegisterRepository(context: Context): RegisterRepository {
        val resources = context.resources
        return RegisterRepository(resources)
    }

    fun provideHomeBottomNavRepository(context: Context): HomeBottomNavRepository {
        val application = MyApplication()
        val pref = DataStorePreferences.getInstance(context.dataStore)
        val database = AppDatabase.getDatabase(context)
        return HomeBottomNavRepository(application, pref, database)
    }

    fun provideDisplayConfigurationRepository(context: Context): DisplayConfigurationRepository {
        val application = MyApplication()
        val pref = DataStorePreferences.getInstance(context.dataStore)
        return DisplayConfigurationRepository(application, pref)
    }

    fun provideMoreMenuRepository(context: Context): MoreMenuRepository {
        val pref = DataStorePreferences.getInstance(context.dataStore)
        return MoreMenuRepository(pref)
    }
}