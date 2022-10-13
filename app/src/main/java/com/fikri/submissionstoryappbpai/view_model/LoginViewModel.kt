package com.fikri.submissionstoryappbpai.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fikri.submissionstoryappbpai.api.ApiConfig
import com.fikri.submissionstoryappbpai.data_model.LoginResponseModel
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.other_class.getStringDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: DataStorePreferences) :
    ViewModel() {

    private val _isShowLoading = MutableLiveData<Boolean>()
    val isShowLoading: LiveData<Boolean> = _isShowLoading
    private val _isShowResponseModal = MutableLiveData<Boolean>()
    val isShowResponseModal: LiveData<Boolean> = _isShowResponseModal
    private val _isTimeToHome = MutableLiveData<Boolean>()
    val isTimeToHome: LiveData<Boolean> = _isTimeToHome

    var firstAppeared = true
    var responseType = ResponseModal.TYPE_GENERAL
    var responseMessage: String? = null
    var loginResponse: LoginResponseModel? = null

    fun login(email: String, password: String) {
        _isShowLoading.value = true
        val apiRequest = ApiConfig().getApiService().login(email, password)
        apiRequest.enqueue(object : Callback<LoginResponseModel> {
            override fun onResponse(
                call: Call<LoginResponseModel>,
                response: Response<LoginResponseModel>
            ) {
                _isShowLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        loginResponse = responseBody
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

            override fun onFailure(call: Call<LoginResponseModel>, t: Throwable) {
                _isShowLoading.value = false
                responseType = ResponseModal.TYPE_ERROR
                _isShowResponseModal.value = true
            }
        })
    }

    fun saveLoginData(userId: String?, name: String?, token: String?) {
        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                pref.saveDataStoreValue(DataStorePreferences.USER_ID_KEY, userId ?: "")
                pref.saveDataStoreValue(DataStorePreferences.NAME_KEY, name ?: "")
                pref.saveDataStoreValue(DataStorePreferences.TOKEN_KEY, token ?: "")
                pref.saveDataStoreValue(DataStorePreferences.SESSION_KEY, getStringDate())
                _isTimeToHome.value = true
            }
        }
    }

    fun dismissResponseModal() {
        _isShowResponseModal.value = false
    }
}