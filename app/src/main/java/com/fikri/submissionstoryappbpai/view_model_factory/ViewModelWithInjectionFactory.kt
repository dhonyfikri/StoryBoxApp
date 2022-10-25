package com.fikri.submissionstoryappbpai.view_model_factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fikri.submissionstoryappbpai.di.Injection
import com.fikri.submissionstoryappbpai.fragment.home_ui_item.more_menu.MoreMenuViewModel
import com.fikri.submissionstoryappbpai.fragment.home_ui_item.story_list.StoryListViewModel
import com.fikri.submissionstoryappbpai.fragment.home_ui_item.story_maps.StoryMapsViewModel
import com.fikri.submissionstoryappbpai.view_model.*

class ViewModelWithInjectionFactory(private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryListViewModel::class.java)) {
            return StoryListViewModel(Injection.provideRepository(context)) as T
        } else if (modelClass.isAssignableFrom(StoryMapsViewModel::class.java)) {
            return StoryMapsViewModel(Injection.provideMapsRepository(context)) as T
        } else if (modelClass.isAssignableFrom(CreateStoryMapViewModel::class.java)) {
            return CreateStoryMapViewModel(Injection.provideCreateMapStoryRepository(context)) as T
        } else if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            return MainActivityViewModel(Injection.provideMainActivityRepository(context)) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(Injection.provideLoginRepository(context)) as T
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(Injection.provideRegisterRepository(context)) as T
        } else if (modelClass.isAssignableFrom(HomeBottomNavViewModel::class.java)) {
            return HomeBottomNavViewModel(Injection.provideHomeBottomNavRepository(context)) as T
        } else if (modelClass.isAssignableFrom(DisplayConfigurationViewModel::class.java)) {
            return DisplayConfigurationViewModel(
                Injection.provideDisplayConfigurationRepository(
                    context
                )
            ) as T
        } else if (modelClass.isAssignableFrom(MoreMenuViewModel::class.java)) {
            return MoreMenuViewModel(Injection.provideMoreMenuRepository(context)) as T
        } else if (modelClass.isAssignableFrom(CreateStoryViewModel::class.java)) {
            return CreateStoryViewModel(Injection.provideCreateStoryRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}