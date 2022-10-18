package com.fikri.submissionstoryappbpai.view_model_factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fikri.submissionstoryappbpai.fragment.home_ui_item.more_menu.MoreMenuViewModel
import com.fikri.submissionstoryappbpai.view_model.CameraShotViewModel
import com.fikri.submissionstoryappbpai.view_model.RegisterViewModel
import com.fikri.submissionstoryappbpai.view_model.StoryDetailViewModel

class ViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel() as T
        } else if (modelClass.isAssignableFrom(StoryDetailViewModel::class.java)) {
            return StoryDetailViewModel() as T
        } else if (modelClass.isAssignableFrom(CameraShotViewModel::class.java)) {
            return CameraShotViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}