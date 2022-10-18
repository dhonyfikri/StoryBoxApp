package com.fikri.submissionstoryappbpai.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.adapter.ListStoryAdapter
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.databinding.ActivityHomeBinding
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.RefreshModal
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.other_class.dataStore
import com.fikri.submissionstoryappbpai.view_model.HomeViewModel
import com.fikri.submissionstoryappbpai.view_model_factory.ViewModelWithDataStorePrefFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    companion object {
        const val CREATE_STORY_RESULT = 100
        const val CREATE_STORY_SUCCESS = "is_successfully_create_story"
    }

    private lateinit var binding: ActivityHomeBinding

    private lateinit var viewModel: HomeViewModel

    private val refreshModal = RefreshModal()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
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
            )[HomeViewModel::class.java]

        binding.apply {
            btnViewMore.alpha = viewModel.btnViewMoreAlpha
            btnViewMore.translationY = viewModel.btnViewMoreTranslateY

            tbHome.overflowIcon =
                ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_dot_three)
            setSupportActionBar(tbHome)
            supportActionBar?.title = null
            supportActionBar?.setHomeActionContentDescription(resources.getString(R.string.open_advanced_menu))

            srlSwipeRefeshStoryList.setColorSchemeColors(
                ContextCompat.getColor(
                    this@HomeActivity,
                    R.color.secondary
                )
            )

            rvStoryList.setHasFixedSize(true)
            rvStoryList.layoutManager = LinearLayoutManager(this@HomeActivity)
            initializeTheStoryListAdapterForTheFirstTime()
        }
    }

    private fun setupAction() {
        binding.apply {
            srlSwipeRefeshStoryList.setOnRefreshListener {
                srlSwipeRefeshStoryList.isRefreshing = false
                if (!viewModel.currentIsLoading) {
                    viewModel.getFreshStoryList()
                }
            }

            fabCreateStory.setOnClickListener {
                fabCreateStory.isEnabled = false
                lifecycleScope.launch(Dispatchers.Default) {
                    delay(800)
                    withContext(Dispatchers.Main) {
                        fabCreateStory.isEnabled = true
                        launcherIntentCreateStory.launch(
                            Intent(
                                this@HomeActivity,
                                CreateStoryActivity::class.java
                            )
                        )
                    }
                }
                fabSplash()
            }

            btnViewMore.setOnClickListener {
                setShowingViewMoreButton(false)
                if (!viewModel.currentIsLoading) {
                    viewModel.loadMore()
                }
            }

            rvStoryList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE && !viewModel.currentIsLoading) {
                        setShowingViewMoreButton(true)
                    } else {
                        setShowingViewMoreButton(false)
                    }
                }
            })
        }

        viewModel.apply {
            isShowLoading.observe(this@HomeActivity) { isShowLoading ->
                if (isShowLoading) {
                    binding.pbLoading.visibility = View.VISIBLE
                    binding.tvStatementNoDataAvailable.visibility = View.GONE
                } else {
                    binding.pbLoading.visibility = View.GONE
                }
            }

            isShowRefreshModal.observe(this@HomeActivity) { isShowingRefreshModal ->
                if (isShowingRefreshModal) {
                    refreshModal.showRefreshModal(
                        this@HomeActivity,
                        responseType,
                        if (responseType != ResponseModal.TYPE_ERROR) {
                            responseMessage
                        } else {
                            resources.getString(
                                R.string.connection_problem
                            )
                        },
                        onRefreshClicked = {
                            viewModel.dismissRefreshModal()
                            viewModel.getAllStories()
                        },
                        onCloseClicked = {
                            viewModel.dismissRefreshModal()
                        }
                    )
                    binding.apply {
                        if (currentStoriesCount > 0) {
                            rvStoryList.visibility = View.VISIBLE
                            tvStatementNoDataAvailable.visibility = View.GONE
                        } else {
                            rvStoryList.visibility = View.GONE
                            tvStatementNoDataAvailable.text =
                                resources.getString(R.string.no_temporary_data)
                            tvStatementNoDataAvailable.visibility = View.VISIBLE
                        }
                    }
                } else {
                    refreshModal.dismiss()
                }
            }

            stories.observe(this@HomeActivity) { stories ->
                setStoryList(stories)
            }
        }
    }

    private fun initializeTheStoryListAdapterForTheFirstTime() {
        val adapterStory = ListStoryAdapter(this, arrayListOf())
        binding.rvStoryList.adapter = adapterStory
    }

    private fun setStoryList(stories: ArrayList<Story>) {
        val adapterStory = ListStoryAdapter(this, stories)
        binding.rvStoryList.adapter = adapterStory

        adapterStory.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
            override fun onClickedItem(data: Story, imageThumbnailsView: View) {
                val moveToStoryDetail = Intent(
                    this@HomeActivity, StoryDetailActivity::class.java
                )
                moveToStoryDetail.putExtra(StoryDetailActivity.EXTRA_STORY, data)
                startActivity(
                    moveToStoryDetail, ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@HomeActivity, Pair(imageThumbnailsView, "image_detail")
                    ).toBundle()
                )
            }
        })

        binding.apply {
            if (stories.size > 0) {
                rvStoryList.visibility = View.VISIBLE
                tvStatementNoDataAvailable.visibility = View.GONE
            } else {
                rvStoryList.visibility = View.GONE
                tvStatementNoDataAvailable.text = resources.getString(R.string.no_data_available)
                tvStatementNoDataAvailable.visibility = View.VISIBLE
            }

            if (viewModel.isLoadMore) {
                rvStoryList.scrollToPosition(viewModel.lastPositionBeforeLoadMore)
            }
        }
    }

    private fun setShowingViewMoreButton(isShowing: Boolean = false) {
        if (isShowing) {
            viewModel.btnViewMoreAlpha = 1f
            viewModel.btnViewMoreTranslateY = 0f
        } else {
            viewModel.btnViewMoreAlpha = 0f
            viewModel.btnViewMoreTranslateY = 400f
        }
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(
                    binding.btnViewMore,
                    View.TRANSLATION_Y,
                    viewModel.btnViewMoreTranslateY
                ).setDuration(300),
                ObjectAnimator.ofFloat(
                    binding.btnViewMore,
                    View.ALPHA,
                    viewModel.btnViewMoreAlpha
                ).setDuration(300)
            )
            start()
        }
    }

    private fun fabSplash() {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val secondaryColor = ContextCompat.getColor(this, R.color.secondary)
        val splash = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(
                    binding.cvSplash,
                    View.SCALE_X,
                    200f
                ).setDuration(1500),
                ObjectAnimator.ofFloat(
                    binding.cvSplash,
                    View.SCALE_Y,
                    200f
                ).setDuration(1500),
                ObjectAnimator.ofArgb(
                    binding.vwSplash,
                    "backgroundColor",
                    secondaryColor, primaryColor
                ).setDuration(1000)
            )
        }
        val disappear = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(
                    binding.cvSplash,
                    View.SCALE_X,
                    0f
                ).setDuration(0),
                ObjectAnimator.ofFloat(
                    binding.cvSplash,
                    View.SCALE_Y,
                    0f
                ).setDuration(0),
                ObjectAnimator.ofArgb(
                    binding.vwSplash,
                    "backgroundColor",
                    primaryColor, secondaryColor
                ).setDuration(0)
            )
        }
        AnimatorSet().apply {
            playSequentially(splash, disappear)
            start()
        }
    }

    private val launcherIntentCreateStory = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == CREATE_STORY_RESULT) {
            if (result.data?.getBooleanExtra(CREATE_STORY_SUCCESS, true) as Boolean) {
                viewModel.getFreshStoryList()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_change_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.action_logout -> {
                viewModel.clearDataStore()
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                finish()
                true
            }
            else -> true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        refreshModal.dismiss()
    }
}