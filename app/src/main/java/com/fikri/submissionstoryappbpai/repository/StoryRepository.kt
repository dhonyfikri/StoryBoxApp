package com.fikri.submissionstoryappbpai.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.*
import com.fikri.submissionstoryappbpai.api.ApiService
import com.fikri.submissionstoryappbpai.data.BasicStoryRemoteMediator
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.database.AppDatabase
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences

class StoryRepository(
    private val pref: DataStorePreferences,
    private val appDatabase: AppDatabase,
    private val apiService: ApiService
) {
    fun getPref() = pref

    fun getBasicStory(token: String): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                initialLoadSize = 4,
                pageSize = 4,
                prefetchDistance = 0
            ),
            remoteMediator = BasicStoryRemoteMediator(appDatabase, apiService, token, pref),
            pagingSourceFactory = {
                appDatabase.storyDao().getAllBasicStory()
            },
        ).liveData
    }

    fun getDatabase() = appDatabase

    fun getCurrentPagingSuccessCode(): LiveData<String> {
        return pref.getDataStoreValue(DataStorePreferences.PAGING_SUCCESS_CODE_KEY).asLiveData()
    }
}