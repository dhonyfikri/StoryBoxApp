package com.fikri.submissionstoryappbpai.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import com.fikri.submissionstoryappbpai.databinding.ActivityMainBinding
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.dataStore
import com.fikri.submissionstoryappbpai.view_model.MainActivityViewModel
import com.fikri.submissionstoryappbpai.view_model_factory.ViewModelWithDataStorePrefFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()
        setupAction()
    }

    private fun setupData() {
        val pref = DataStorePreferences.getInstance(dataStore)
        viewModel =
            ViewModelProvider(
                this,
                ViewModelWithDataStorePrefFactory(pref)
            )[MainActivityViewModel::class.java]
    }

    private fun setupAction() {
        viewModel.apply {
            viewModel.getThemeSettings().observe(this@MainActivity) { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }

            isTimeOut.observe(this@MainActivity) { isTimeOut ->
                if (isTimeOut) {
                    viewModel.validatingLoginSession()
                }
            }

            isValidSession.observe(this@MainActivity) { isValidated ->
                if (isValidated) {
                    saveCurrentSession()
                } else {
                    startActivity(
                        Intent(this@MainActivity, LoginActivity::class.java),
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this@MainActivity, Pair(binding.view, "logo_wrapper"),
                            Pair(binding.ivLogoFrame, "logo_frame"),
                            Pair(binding.ivLogoText, "logo_text"),
                            Pair(binding.ivLogoFeather, "logo_feather"),
                            Pair(binding.ivLogoSpot, "logo_spot")
                        ).toBundle()
                    )
                }
            }

            isTimeToHome.observe(this@MainActivity) {
                if (it) {
                    startActivity(
                        Intent(this@MainActivity, HomeBottomNavigationActivity::class.java)
                    )
                }
            }
        }
    }
}