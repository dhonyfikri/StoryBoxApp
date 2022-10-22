package com.fikri.submissionstoryappbpai.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.repository.HomeBottomNavRepository

class HomeBottomNavViewModel(private val pref: DataStorePreferences) : ViewModel() {

    val storyListIcon = R.drawable.ic_list
    val storyMapsIcon = R.drawable.ic_map
    val createStoryIcon = R.drawable.ic_create_story
    val moreMenuIcon = R.drawable.ic_dashboard

    private val _headerIcon = MutableLiveData(storyListIcon)
    val headerIcon: LiveData<Int> = _headerIcon

    var navController: NavController? = null
    var requestToRefreshAdapterAfterAddNewPost = false

    fun changeHeaderIcon(icon: Int) {
        _headerIcon.value = icon
    }

    fun clearDataStore() = HomeBottomNavRepository().clearDataStore(pref)
}