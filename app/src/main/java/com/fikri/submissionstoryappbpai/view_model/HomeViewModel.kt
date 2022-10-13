package com.fikri.submissionstoryappbpai.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fikri.submissionstoryappbpai.api.ApiConfig
import com.fikri.submissionstoryappbpai.data_model.AllStoryResponseModel
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.RefreshModal
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val pref: DataStorePreferences) : ViewModel() {

    private val _stories = MutableLiveData<ArrayList<Story>>()
    val stories: LiveData<ArrayList<Story>> = _stories
    private val _isShowLoading = MutableLiveData(true)
    val isShowLoading: LiveData<Boolean> = _isShowLoading
    private val _isShowRefreshModal = MutableLiveData<Boolean>()
    val isShowRefreshModal: LiveData<Boolean> = _isShowRefreshModal

    var responseType = ResponseModal.TYPE_GENERAL
    var responseMessage: String? = null
    var currentStoriesCount = 0
    var currentIsLoading = false
    var btnViewMoreAlpha = 0f
    var btnViewMoreTranslateY = 400f
    var requestSize = 10
    var isLoadMore = false
    var lastPositionBeforeLoadMore = 0

    init {
        getAllStories()
    }

    fun getFreshStoryList(){
        isLoadMore = false
        requestSize = 10
        getAllStories()
    }

    fun loadMore(){
        lastPositionBeforeLoadMore = currentStoriesCount - 1
        isLoadMore = true
        requestSize += 10
        getAllStories()
    }

    fun getAllStories() {
        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                val token = pref.getDataStoreValue(DataStorePreferences.TOKEN_KEY).first()
                fetchAllStories(token)
            }
        }
    }

    private fun fetchAllStories(token: String) {
        _isShowLoading.value = true
        currentIsLoading = true
        val apiRequest = ApiConfig().getApiService().getAllStories("Bearer $token", requestSize, 0)
        apiRequest.enqueue(object : Callback<AllStoryResponseModel> {
            override fun onResponse(
                call: Call<AllStoryResponseModel>,
                response: Response<AllStoryResponseModel>
            ) {
                _isShowLoading.value = false
                currentIsLoading = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        currentStoriesCount = responseBody.listStory.size
                        requestSize = responseBody.listStory.size
                        _stories.value = responseBody.listStory
                    } else {
                        responseType = RefreshModal.TYPE_FAILED
                        responseMessage = responseBody?.message
                        _isShowRefreshModal.value = true
                    }
                } else {
                    var errorMessage: String? = null
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        errorMessage = jObjError.getString("message")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    responseType = RefreshModal.TYPE_FAILED
                    responseMessage = "${response.code()} ${response.message()} | $errorMessage"
                    _isShowRefreshModal.value = true
                }
            }

            override fun onFailure(call: Call<AllStoryResponseModel>, t: Throwable) {
                _isShowLoading.value = false
                currentIsLoading = false
                responseType = RefreshModal.TYPE_ERROR
                _isShowRefreshModal.value = true
            }
        })
    }

    fun clearDataStore() {
        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                pref.clearDataStore()
            }
        }
    }

    fun dismissRefreshModal() {
        _isShowRefreshModal.value = false
    }
}