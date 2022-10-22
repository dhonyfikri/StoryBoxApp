package com.fikri.submissionstoryappbpai.fragment.home_ui_item.story_maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fikri.submissionstoryappbpai.data_model.CameraMapPosition
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.other_class.RefreshModal
import com.fikri.submissionstoryappbpai.repository.MapsStoryRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoryMapsViewModel(private val mapsStoryRepository: MapsStoryRepository) : ViewModel() {

    private val _stories = MutableLiveData<ArrayList<Story>>()
    val stories: LiveData<ArrayList<Story>> = _stories

    val mapModeInSetting = mapsStoryRepository.getMapMode()

    var currentCameraPosition = CameraMapPosition(StoryMapsFragment.INITIAL_FOCUS, 5f, 0f)
    var storyOnMap = ArrayList<Story>()
    var hybridTranslationX = 0f
    var satelliteTranslationX = 0f
    var normalTranslationX = 0f
    var toggleModeTranslationX = 0f
    var previewAlpha = 0f
    var selectedStory: Story? = null
    var asu: Story? = null

    var selectedMarkerObject: Marker? = null
    var selectedMarkerObjectId: String? = null

    var mToken = ""

    var isShowingMapModeOptions = false
    var isShowingPreview = false
    var responseType = RefreshModal.TYPE_GENERAL
    var responseMessage = ""
    var currentMapMode: String? = null
    var firstAppeared = true

    init {
        getToken()
    }

    private fun getToken() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val tokenResult = mapsStoryRepository.getToken()
                mToken = tokenResult
                getStories(tokenResult)
            }
        }
    }

    fun getStories(token: String) {
        mapsStoryRepository.getMapsStory(
            token,
            onSuccess = { listStory, message ->
                responseMessage = message
                _stories.value = listStory
            },
            onFailed = { responseType, message ->
                this.responseType = responseType
                responseMessage = message
            }
        )
    }

    fun getAddressName(latLng: LatLng) = mapsStoryRepository.getAddressName(latLng)
}