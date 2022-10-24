package com.fikri.submissionstoryappbpai.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.repository.RegisterRepository

class RegisterViewModel(private val registerRepository: RegisterRepository) :
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
        registerRepository.register(name, email, password) { responseType, responseMessage ->
            _isShowLoading.value = false
            this.responseType = responseType
            this.responseMessage = responseMessage
            _isShowResponseModal.value = true
        }
    }

    fun dismissResponseModal() {
        _isShowResponseModal.value = false
    }
}