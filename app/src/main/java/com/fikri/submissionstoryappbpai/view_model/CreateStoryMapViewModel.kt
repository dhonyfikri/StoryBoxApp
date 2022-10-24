package com.fikri.submissionstoryappbpai.view_model

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fikri.submissionstoryappbpai.activity.CreateStoryMapActivity
import com.fikri.submissionstoryappbpai.data_model.CameraMapPosition
import com.fikri.submissionstoryappbpai.other_class.RefreshModal
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.repository.CreateStoryMapRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class CreateStoryMapViewModel(private val createStoryMapRepository: CreateStoryMapRepository) :
    ViewModel() {
    private val _photoBitmap = MutableLiveData<Bitmap>()
    val photoBitmap: LiveData<Bitmap> = _photoBitmap
    private val _isShowResponseModal = MutableLiveData<Boolean>()
    val isShowResponseModal: LiveData<Boolean> = _isShowResponseModal
    private val _isAddButtonEnabled = MutableLiveData(false)
    val isAddButtonEnabled: LiveData<Boolean> = _isAddButtonEnabled
    private val _isShowLoading = MutableLiveData<Boolean>()
    val isShowLoading: LiveData<Boolean> = _isShowLoading
    private val _isShowRefreshModal = MutableLiveData<Boolean>()
    val isShowRefreshModal: LiveData<Boolean> = _isShowRefreshModal

    val mapModeInSetting = createStoryMapRepository.getMapMode()

    var currentCameraPosition = CameraMapPosition(CreateStoryMapActivity.INITIAL_FOCUS, 5f, 0f)
    var hybridTranslationX = 0f
    var satelliteTranslationX = 0f
    var normalTranslationX = 0f
    var toggleModeTranslationX = 0f
    var photo: File? = null
    private var mToken = ""
    var selectedPosition: LatLng? = null

    var isShowingMapModeOptions = false
    var responseType = RefreshModal.TYPE_GENERAL
    var responseMessage: String? = null
    var currentMapMode: String? = null
    var firstAppeared = true

    init {
        getToken()
    }

    private fun getToken() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                val tokenResult = createStoryMapRepository.getToken()
                mToken = tokenResult
            }
        }
    }

    fun uploadStory(desc: String) {
        if (mToken.isNotEmpty()) {
            _isShowLoading.value = true
            createStoryMapRepository.postDataToServer(
                mToken,
                desc,
                selectedPosition as LatLng,
                photo
            ) { _responseType, _responseMessage ->
                _isShowLoading.value = false
                responseType = _responseType
                responseMessage = _responseMessage
                if (_responseType == ResponseModal.TYPE_SUCCESS) {
                    _isShowResponseModal.value = true
                } else {
                    _isShowRefreshModal.value = true
                }

            }
        }
    }

    fun getAddressName(latLng: LatLng) = createStoryMapRepository.getAddressName(latLng)

    fun setPhotoBitmap(value: Bitmap) {
        _photoBitmap.value = value
    }

    fun setAddButtonEnable(value: Boolean) {
        _isAddButtonEnabled.value = value
    }

    fun dismissResponseModal() {
        _isShowResponseModal.value = false
    }

    fun dismissRefreshModal() {
        _isShowRefreshModal.value = false
    }
}