package com.fikri.submissionstoryappbpai.view_model

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.repository.HomeBottomNavRepository

class HomeBottomNavViewModel(private val pref: DataStorePreferences) : ViewModel() {
    var navController: NavController? = null

    fun clearDataStore() = HomeBottomNavRepository().clearDataStore(pref)
}