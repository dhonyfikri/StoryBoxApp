package com.fikri.submissionstoryappbpai.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.getDayDiff
import com.fikri.submissionstoryappbpai.other_class.getStringDate
import com.fikri.submissionstoryappbpai.other_class.toDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel(private val pref: DataStorePreferences) : ViewModel() {
    private val _isTimeOut = MutableLiveData(false)
    val isTimeOut: LiveData<Boolean> = _isTimeOut
    private val _timeToSelfDestroy = MutableLiveData(false)
    val timeToSelfDestroy: LiveData<Boolean> = _timeToSelfDestroy
    private val _isValidSession = MutableLiveData<Boolean>()
    val isValidSession: LiveData<Boolean> = _isValidSession
    private val _isTimeToHome = MutableLiveData<Boolean>()
    val isTimeToHome: LiveData<Boolean> = _isTimeToHome

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

    fun waitBeforeSelfDestroy() {
        viewModelScope.launch(Dispatchers.Default) {
            delay(1000)
            withContext(Dispatchers.Main) {
                _timeToSelfDestroy.value = true
            }
        }
    }

    fun validatingLoginSession() {
        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                val currentDate = getStringDate().toDate()
                val lastLoginDate =
                    pref.getDataStoreValue(DataStorePreferences.SESSION_KEY).first().toDate()
                val token = pref.getDataStoreValue(DataStorePreferences.TOKEN_KEY).first()
                _isValidSession.value =
                    getDayDiff(lastLoginDate, currentDate) < 1 && token.isNotEmpty()
            }
        }
    }

    fun saveCurrentSession() {
        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                pref.saveDataStoreValue(DataStorePreferences.SESSION_KEY, getStringDate())
                _isTimeToHome.value = true
            }
        }
    }
}