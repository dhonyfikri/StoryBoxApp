package com.fikri.submissionstoryappbpai.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fikri.submissionstoryappbpai.data_model.LoginResponseModel
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val loginRepository: LoginRepository) :
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
    private var loginResponse: LoginResponseModel? = null

    fun login(email: String, password: String) {
        _isShowLoading.value = true
        loginRepository.login(
            email,
            password
        ) { responseType, responseMessage, loginResponse ->
            _isShowLoading.value = false
            if (responseType == ResponseModal.TYPE_SUCCESS && loginResponse != null) {
                this.loginResponse = loginResponse
                saveLoginData()
            } else {
                this.responseType = responseType
                this.responseMessage = responseMessage
                _isShowResponseModal.value = true
            }
        }
    }

    private fun saveLoginData() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                loginRepository.saveLoginData(loginResponse?.loginResult)
                _isTimeToHome.value = true
            }
        }
    }

    fun dismissResponseModal() {
        _isShowResponseModal.value = false
    }
}