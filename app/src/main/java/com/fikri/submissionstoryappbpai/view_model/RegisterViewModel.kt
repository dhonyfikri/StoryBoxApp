package com.fikri.submissionstoryappbpai.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fikri.submissionstoryappbpai.api.ApiConfig
import com.fikri.submissionstoryappbpai.data_model.RegisterResponseModel
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel :
    ViewModel() {

    private val _isShowLoading = MutableLiveData<Boolean>()
    val isShowLoading: LiveData<Boolean> = _isShowLoading
    private val _isShowResponseModal = MutableLiveData<Boolean>()
    val isShowResponseModal: LiveData<Boolean> = _isShowResponseModal

    var firstAppeared = true
    var responseType = ResponseModal.TYPE_GENERAL
    var responseMessage: String? = null

    fun register(name: String, email: String, password: String) {
        _isShowLoading.value = true
        val apiRequest = ApiConfig().getApiService().register(name, email, password)
        apiRequest.enqueue(object : Callback<RegisterResponseModel> {
            override fun onResponse(
                call: Call<RegisterResponseModel>,
                response: Response<RegisterResponseModel>
            ) {
                _isShowLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        responseType = ResponseModal.TYPE_SUCCESS
                        responseMessage = responseBody.message
                        _isShowResponseModal.value = true
                    } else {
                        responseType = ResponseModal.TYPE_FAILED
                        responseMessage = responseBody?.message
                        _isShowResponseModal.value = true
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
                    _isShowResponseModal.value = true
                }
            }

            override fun onFailure(call: Call<RegisterResponseModel>, t: Throwable) {
                _isShowLoading.value = false
                responseType = ResponseModal.TYPE_ERROR
                _isShowResponseModal.value = true
            }
        })
    }

    fun dismissResponseModal() {
        _isShowResponseModal.value = false
    }
}