package com.fikri.submissionstoryappbpai.view_model_factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fikri.submissionstoryappbpai.di.Injection
import com.fikri.submissionstoryappbpai.fragment.home_ui_item.story_list.StoryListViewModel
import com.fikri.submissionstoryappbpai.fragment.home_ui_item.story_maps.StoryMapsViewModel

class ViewModelWithInjectionFactory(private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryListViewModel::class.java)) {
            return StoryListViewModel(Injection.provideRepository(context)) as T
        } else if (modelClass.isAssignableFrom(StoryMapsViewModel::class.java)) {
            return StoryMapsViewModel(Injection.provideMapsRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}