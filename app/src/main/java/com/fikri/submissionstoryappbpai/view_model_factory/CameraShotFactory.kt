package com.fikri.submissionstoryappbpai.view_model_factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fikri.submissionstoryappbpai.view_model.CameraShotViewModel

class CameraShotFactory : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CameraShotViewModel() as T
    }
}