package com.fikri.submissionstoryappbpai.view_model

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fikri.submissionstoryappbpai.api.ApiConfig
import com.fikri.submissionstoryappbpai.data_model.AddStoryResponseModel
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.other_class.reduceFileImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    fun uploadStory(desc: String) {
        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                val token = pref.getDataStoreValue(DataStorePreferences.TOKEN_KEY).first()
                postDataToServer(token, desc)
            }
        }
    }

    private fun postDataToServer(token: String, desc: String){
        _isShowLoadingModal.value = true
        val photoFile = reduceFileImage(photo as File)
        val description = desc.trim().toRequestBody("text/plain".toMediaType())
        val requestImageFile = photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            photoFile.name,
            requestImageFile
        )
        val apiRequest = ApiConfig().getApiService().addStory("Bearer $token", imageMultipart, description)
        apiRequest.enqueue(object : Callback<AddStoryResponseModel> {
            override fun onResponse(
                call: Call<AddStoryResponseModel>,
                response: Response<AddStoryResponseModel>
            ) {
                _isShowLoadingModal.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        responseType = ResponseModal.TYPE_SUCCESS
                        responseMessage = responseBody.message
                        _isShowResponseModal.value = true
                    } else {
                        responseType = ResponseModal.TYPE_FAILED
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
                    responseType = ResponseModal.TYPE_MISTAKE
                    responseMessage = "${response.code()} ${response.message()} | $errorMessage"
                    _isShowRefreshModal.value = true
                }
            }

            override fun onFailure(call: Call<AddStoryResponseModel>, t: Throwable) {
                _isShowLoadingModal.value = false
                responseType = ResponseModal.TYPE_ERROR
                _isShowRefreshModal.value = true
            }
        })
    }

    fun setPhotoBitmap(value: Bitmap){
        _photoBitmap.value = value
    }

    fun setAddButtonEnable(value: Boolean){
        _isAddButtonEnabled.value = value
    }

    fun dismissRefreshModal() {
        _isShowRefreshModal.value = false
    }

    fun dismissResponseModal() {
        _isShowResponseModal.value = false
    }
}