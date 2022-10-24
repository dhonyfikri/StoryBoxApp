package com.fikri.submissionstoryappbpai.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fikri.submissionstoryappbpai.repository.MainActivityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel(private val mainActivityRepository: MainActivityRepository) :
    ViewModel() {
    private val _isTimeOut = MutableLiveData(false)
    val isTimeOut: LiveData<Boolean> = _isTimeOut
    private val _isValidSession = MutableLiveData<Boolean>()
    val isValidSession: LiveData<Boolean> = _isValidSession
    private val _isTimeToHome = MutableLiveData<Boolean>()
    val isTimeToHome: LiveData<Boolean> = _isTimeToHome

    var themeSetting = mainActivityRepository.getThemeSettings()

    init {
        waitAMoment()
    }

    private fun waitAMoment() {
        viewModelScope.launch(Dispatchers.Default) {
            delay(2500)
            withContext(Dispatchers.Main) {
                _isTimeOut.value = true
            }
        }
    }

    fun validatingLoginSession() {
        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                _isValidSession.value =
                    mainActivityRepository.validatingLoginSession()
            }
        }
    }

    fun saveCurrentSession() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                mainActivityRepository.saveCurrentSession()
                _isTimeToHome.value = true
            }
        }
    }
}