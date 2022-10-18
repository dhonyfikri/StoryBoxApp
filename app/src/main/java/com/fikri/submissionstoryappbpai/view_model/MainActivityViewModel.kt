package com.fikri.submissionstoryappbpai.view_model

import androidx.lifecycle.*
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.repository.MainActivityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel(private val pref: DataStorePreferences) : ViewModel() {
    private val _isTimeOut = MutableLiveData(false)
    val isTimeOut: LiveData<Boolean> = _isTimeOut
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

    fun validatingLoginSession() {
        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                _isValidSession.value =
                    MainActivityRepository().validatingLoginSession(pref)
            }
        }
    }

    fun saveCurrentSession() {
        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                MainActivityRepository().saveCurrentSession(pref)
                _isTimeToHome.value = true
            }
        }
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getDataStoreValue(DataStorePreferences.DARK_MODE_KEY).asLiveData()
    }
}