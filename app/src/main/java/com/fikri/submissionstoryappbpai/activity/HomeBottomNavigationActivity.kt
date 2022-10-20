package com.fikri.submissionstoryappbpai.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.databinding.ActivityHomeBottomNavigationBinding
import com.fikri.submissionstoryappbpai.fragment.home_ui_item.create_story_options.CreateStoryOptionsFragment
import com.fikri.submissionstoryappbpai.fragment.home_ui_item.more_menu.MoreMenuFragment
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.dataStore
import com.fikri.submissionstoryappbpai.view_model.HomeBottomNavViewModel
import com.fikri.submissionstoryappbpai.view_model_factory.ViewModelWithDataStorePrefFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class HomeBottomNavigationActivity : AppCompatActivity(), MoreMenuFragment.SettingFragListener,
    CreateStoryOptionsFragment.CreateStoryOptionsListener {

    private lateinit var binding: ActivityHomeBottomNavigationBinding
    private lateinit var navView: BottomNavigationView

    private lateinit var viewModel: HomeBottomNavViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = DataStorePreferences.getInstance(dataStore)
        viewModel =
            ViewModelProvider(
                this,
                ViewModelWithDataStorePrefFactory(pref)
            )[HomeBottomNavViewModel::class.java]

        navView = binding.navView

        viewModel.navController =
            findNavController(R.id.nav_host_fragment_activity_home_bottom_navigation)

        navView.setupWithNavController(viewModel.navController!!)
    }

    private val bottomNavListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_story_list -> {
                    binding.ivHeaderIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_list
                        )
                    )
                    binding.tvHeaderTitle.text = getString(R.string.story_list)
                }
                R.id.navigation_story_maps -> {
                    binding.ivHeaderIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_map
                        )
                    )
                    binding.tvHeaderTitle.text = getString(R.string.geolocation_stories)
                }
                R.id.navigation_create_story_options -> {
                    binding.ivHeaderIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_create_story
                        )
                    )
                    binding.tvHeaderTitle.text = getString(R.string.create_a_new_story)
                }
                R.id.navigation_more_menu -> {
                    binding.ivHeaderIcon.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_dashboard
                        )
                    )
                    binding.tvHeaderTitle.text = getString(R.string.more_menu)
                }
            }
        }

    private fun getFragment(): Fragment? {
        val navHostFragment: Fragment? = supportFragmentManager.primaryNavigationFragment
        return navHostFragment?.childFragmentManager?.fragments?.get(0)
    }

    override fun onCreateStoryOptionsClicked(options: String) {
        when (options) {
            CreateStoryOptionsFragment.CREATE_BASIC_STORY -> {
                startActivity(
                    Intent(
                        this@HomeBottomNavigationActivity,
                        CreateStoryActivity::class.java
                    )
                )
            }
            CreateStoryOptionsFragment.CREATE_GEOLOCATION_STORY -> {
                startActivity(
                    Intent(
                        this@HomeBottomNavigationActivity,
                        CreateStoryActivity::class.java
                    )
                )
            }
        }
    }

    override fun onOptionClicked(item: String) {
        when (item) {
            MoreMenuFragment.APPLICATION_DETAILS_OPTIONS -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            MoreMenuFragment.DISPLAY_OPTIONS -> {
                startActivity(
                    Intent(
                        this@HomeBottomNavigationActivity,
                        DisplayConfigurationActivity::class.java
                    )
                )
            }
        }
    }

    override fun onLogoutRequest() {
        viewModel.clearDataStore()
        finish()
        startActivity(Intent(this@HomeBottomNavigationActivity, LoginActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        viewModel.navController?.addOnDestinationChangedListener(bottomNavListener)
    }

    override fun onPause() {
        super.onPause()
        viewModel.navController?.removeOnDestinationChangedListener(bottomNavListener)
    }
}