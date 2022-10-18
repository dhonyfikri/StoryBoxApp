package com.fikri.submissionstoryappbpai.fragment.home_ui_item.create_story_options

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateStoryOptionsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is story options Fragment"
    }
    val text: LiveData<String> = _text
}