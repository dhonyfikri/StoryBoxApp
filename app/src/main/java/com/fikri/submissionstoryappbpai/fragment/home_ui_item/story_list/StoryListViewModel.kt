package com.fikri.submissionstoryappbpai.fragment.home_ui_item.story_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.repository.BasicStoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoryListViewModel(private val basicStoryRepository: BasicStoryRepository) : ViewModel() {

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token
    private val _showEmptyStoryMessage = MutableLiveData<Boolean>()
    val showEmptyStoryMessage: LiveData<Boolean> = _showEmptyStoryMessage
    private val _adapterStillLoading = MutableLiveData<Boolean>()
    val adapterStillLoading: LiveData<Boolean> = _adapterStillLoading

    lateinit var stories: LiveData<PagingData<Story>>

    var storyCountInDatabase = 0
    var storyAdapterIsEmpty = false
    var adapterInitialLoading = false

    var offlineAlertAlpha = 0f
    var offlineAlertTranslationY = -120f

    init {
        getToken()
    }

    private fun getToken() {
        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                val tokenResult =
                    basicStoryRepository.getPref().getDataStoreValue(DataStorePreferences.TOKEN_KEY)
                        .first()
                prepareStoryPaging(tokenResult)
                _token.value = tokenResult
            }
        }
    }

    private fun prepareStoryPaging(token: String) {
        stories = basicStoryRepository.getBasicStory(token).cachedIn(viewModelScope)
    }

    fun getStoryCount(): LiveData<Int> = basicStoryRepository.getDatabase().storyDao().getBasicStoryCount()

    fun syncStoryListEmptyState() {
        _showEmptyStoryMessage.value = storyAdapterIsEmpty && storyCountInDatabase == 0
    }

    fun changeAdapterLoadingState(isLoading: Boolean) {
        _adapterStillLoading.value = isLoading
    }
}