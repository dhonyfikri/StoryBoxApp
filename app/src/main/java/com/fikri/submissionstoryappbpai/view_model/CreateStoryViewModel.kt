package com.fikri.submissionstoryappbpai.view_model

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.repository.CreateStoryRepository
import java.io.File

class CreateStoryViewModel(private val pref: DataStorePreferences) :
    ViewModel() {

    private val _photoBitmap = MutableLiveData<Bitmap>()
    val photoBitmap: LiveData<Bitmap> = _photoBitmap
    private val _isShowLoadingModal = MutableLiveData<Boolean>()
    val isShowLoadingModal: LiveData<Boolean> = _isShowLoadingModal
    private val _isShowRefreshModal = MutableLiveData<Boolean>()
    val isShowRefreshModal: LiveData<Boolean> = _isShowRefreshModal
    private val _isShowResponseModal = MutableLiveData<Boolean>()
    val isShowResponseModal: LiveData<Boolean> = _isShowResponseModal
    private val _isAddButtonEnabled = MutableLiveData(false)
    val isAddButtonEnabled: LiveData<Boolean> = _isAddButtonEnabled

    var photo: File? = null
    var firstAppeared = true
    var responseType = ResponseModal.TYPE_GENERAL
    var responseMessage: String? = null

    fun uploadStory(desc: String) = CreateStoryRepository().fetchToken(pref) { token ->
        postDataToServer(token, desc)
    }

    private fun postDataToServer(token: String, desc: String) {
        _isShowLoadingModal.value = true
        CreateStoryRepository().postDataToServer(
            token,
            desc,
            photo
        ) { _responseType, _responseMessage ->
            _isShowLoadingModal.value = false
            responseType = _responseType
            responseMessage = _responseMessage
            if (_responseType == ResponseModal.TYPE_SUCCESS) {
                _isShowResponseModal.value = true
            } else {
                _isShowRefreshModal.value = true
            }
        }
    }

    fun setPhotoBitmap(value: Bitmap) {
        _photoBitmap.value = value
    }

    fun setAddButtonEnable(value: Boolean) {
        _isAddButtonEnabled.value = value
    }

    fun dismissRefreshModal() {
        _isShowRefreshModal.value = false
    }

    fun dismissResponseModal() {
        _isShowResponseModal.value = false
    }
}