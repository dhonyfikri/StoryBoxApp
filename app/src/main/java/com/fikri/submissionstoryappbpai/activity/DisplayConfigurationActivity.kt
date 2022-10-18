package com.fikri.submissionstoryappbpai.activity

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.provider.Settings
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.databinding.ActivityDisplayConfigurationBinding
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.dataStore
import com.fikri.submissionstoryappbpai.repository.DisplayConfigurationViewModel
import com.fikri.submissionstoryappbpai.view_model_factory.ViewModelWithDataStorePrefFactory

class DisplayConfigurationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisplayConfigurationBinding

    private lateinit var viewModel: DisplayConfigurationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayConfigurationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = DataStorePreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(
            this,
            ViewModelWithDataStorePrefFactory(pref)
        )[DisplayConfigurationViewModel::class.java]

        binding.swThemeSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.saveThemeSetting(isChecked)
        }

        binding.llLanguageOptions.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        viewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.ivThemeMode.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_night_mode
                    )
                )
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.ivThemeMode.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_light_mode
                    )
                )
            }
            binding.ivThemeMode.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(this, R.color.secondary),
                PorterDuff.Mode.SRC_IN
            )
            binding.swThemeSwitch.isChecked = isDarkModeActive
        }
    }
}