package com.fikri.submissionstoryappbpai.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.repository.HomeRepository

class HomeViewModel(private val pref: DataStorePreferences) : ViewModel() {

    private val _stories = MutableLiveData<ArrayList<Story>>()
    val stories: LiveData<ArrayList<Story>> = _stories
    private val _isShowLoading = MutableLiveData(true)
    val isShowLoading: LiveData<Boolean> = _isShowLoading
    private val _isShowRefreshModal = MutableLiveData<Boolean>()
    val isShowRefreshModal: LiveData<Boolean> = _isShowRefreshModal

    var responseType: String? = ResponseModal.TYPE_GENERAL
    var responseMessage: String? = null
    var currentStoriesCount = 0
    var currentIsLoading = false
    var btnViewMoreAlpha = 0f
    var btnViewMoreTranslateY = 400f
    private var requestSize = 10
    var isLoadMore = false
    var lastPositionBeforeLoadMore = 0

    init {
        getAllStories()
    }

    fun getFreshStoryList() {
        isLoadMore = false
        requestSize = 10
        getAllStories()
    }

    fun loadMore() {
        lastPositionBeforeLoadMore = currentStoriesCount - 1
        isLoadMore = true
        requestSize += 10
        getAllStories()
    }

    fun getAllStories() = HomeRepository().getAllStories(pref) { token ->
        fetchAllStories(token)
    }

    private fun fetchAllStories(token: String) {
        _isShowLoading.value = true
        currentIsLoading = true
        HomeRepository().fetchAllStories(token, requestSize) { _currentStoriesCount,
                                                               _storyList,
                                                               _responseType,
                                                               _responseMessage ->
            _isShowLoading.value = false
            currentIsLoading = false
            if (_responseType == null) {
                currentStoriesCount = _currentStoriesCount ?: 0
                requestSize = currentStoriesCount
                _stories.value = _storyList
            } else {
                requestSize = if (currentStoriesCount != 0) currentStoriesCount else requestSize
                responseType = _responseType
                responseMessage = _responseMessage
                _isShowRefreshModal.value = true
            }
        }
    }

    fun clearDataStore() = HomeRepository().clearDataStore(pref)

    fun dismissRefreshModal() {
        _isShowRefreshModal.value = false
    }
}