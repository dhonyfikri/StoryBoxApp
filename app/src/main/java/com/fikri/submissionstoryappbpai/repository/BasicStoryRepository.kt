package com.fikri.submissionstoryappbpai.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.fikri.submissionstoryappbpai.api.ApiService
import com.fikri.submissionstoryappbpai.data.BasicStoryRemoteMediator
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.database.AppDatabase
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences

class BasicStoryRepository(
    private val pref: DataStorePreferences,
    private val appDatabase: AppDatabase,
    private val apiService: ApiService
) {
    fun getPref() = pref

    fun getBasicStory(token: String): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                initialLoadSize = 20,
                pageSize = 10,
                prefetchDistance = 5
            ),
            remoteMediator = BasicStoryRemoteMediator(appDatabase, apiService, token),
            pagingSourceFactory = {
                appDatabase.storyDao().getAllBasicStory()
            }
        ).liveData
    }

    fun getDatabase() = appDatabase
}