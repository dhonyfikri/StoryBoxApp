package com.fikri.submissionstoryappbpai.view_model_factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fikri.submissionstoryappbpai.fragment.home_ui_item.more_menu.MoreMenuViewModel
import com.fikri.submissionstoryappbpai.fragment.home_ui_item.story_list.StoryListViewModel
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.repository.DisplayConfigurationViewModel
import com.fikri.submissionstoryappbpai.view_model.*

class ViewModelWithDataStorePrefFactory(private val pref: DataStorePreferences) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(pref) as T
        } else if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(pref) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(pref) as T
        } else if (modelClass.isAssignableFrom(CreateStoryViewModel::class.java)) {
            return CreateStoryViewModel(pref) as T
        } else if (modelClass.isAssignableFrom(HomeBottomNavViewModel::class.java)) {
            return HomeBottomNavViewModel(pref) as T
        } else if (modelClass.isAssignableFrom(DisplayConfigurationViewModel::class.java)) {
            return DisplayConfigurationViewModel(pref) as T
        } else if (modelClass.isAssignableFrom(MoreMenuViewModel::class.java)) {
            return MoreMenuViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}