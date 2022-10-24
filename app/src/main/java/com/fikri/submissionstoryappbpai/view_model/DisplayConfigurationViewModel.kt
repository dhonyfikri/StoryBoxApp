package com.fikri.submissionstoryappbpai.view_model

import androidx.lifecycle.ViewModel
import com.fikri.submissionstoryappbpai.repository.DisplayConfigurationRepository

class DisplayConfigurationViewModel(private val displayConfigurationRepository: DisplayConfigurationRepository) :
    ViewModel() {
    fun getThemeSettings() = displayConfigurationRepository.getThemeSettings()

    fun saveThemeSetting(isDarkModeActive: Boolean) =
        displayConfigurationRepository.saveThemeSetting(isDarkModeActive)

    fun getMapMode() = displayConfigurationRepository.getMapMode()

    fun saveMapMode(type: String) = displayConfigurationRepository.saveMapMode(type)
}