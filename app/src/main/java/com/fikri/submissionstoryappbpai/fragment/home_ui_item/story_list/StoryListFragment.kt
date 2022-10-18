package com.fikri.submissionstoryappbpai.fragment.home_ui_item.story_list

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.activity.RegisterActivity
import com.fikri.submissionstoryappbpai.activity.StoryDetailActivity
import com.fikri.submissionstoryappbpai.adapter.ListStoryAdapter
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.databinding.FragmentStoryListBinding
import com.fikri.submissionstoryappbpai.other_class.DataStorePreferences
import com.fikri.submissionstoryappbpai.other_class.RefreshModal
import com.fikri.submissionstoryappbpai.other_class.ResponseModal
import com.fikri.submissionstoryappbpai.other_class.dataStore
import com.fikri.submissionstoryappbpai.view_model_factory.ViewModelWithDataStorePrefFactory

class StoryListFragment : Fragment() {

    private var _binding: FragmentStoryListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: StoryListViewModel
    private lateinit var ctx: Context
    private val refreshModal = RefreshModal()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupData()
        setupAction()
    }

    private fun setupData() {
        val pref = DataStorePreferences.getInstance(context?.dataStore!!)
        viewModel = ViewModelProvider(
            this,
            ViewModelWithDataStorePrefFactory(pref)
        )[StoryListViewModel::class.java]

        binding.apply {
            btnViewMore.alpha = viewModel.btnViewMoreAlpha
            btnViewMore.translationY = viewModel.btnViewMoreTranslateY

//            tbHome.overflowIcon =
//                ContextCompat.getDrawable(this@HomeActivity, R.drawable.ic_dot_three)
//            setSupportActionBar(tbHome)
//            supportActionBar?.title = null
//            supportActionBar?.setHomeActionContentDescription(resources.getString(R.string.open_advanced_menu))

            srlSwipeRefeshStoryList.setColorSchemeColors(
                ContextCompat.getColor(
                    ctx,
                    R.color.secondary
                )
            )

            rvStoryList.setHasFixedSize(true)
            rvStoryList.layoutManager = LinearLayoutManager(ctx)
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

//            fabCreateStory.setOnClickListener {
//                fabCreateStory.isEnabled = false
//                lifecycleScope.launch(Dispatchers.Default) {
//                    delay(800)
//                    withContext(Dispatchers.Main) {
//                        fabCreateStory.isEnabled = true
//                        launcherIntentCreateStory.launch(
//                            Intent(
//                                this@HomeActivity,
//                                CreateStoryActivity::class.java
//                            )
//                        )
//                    }
//                }
//                fabSplash()
//            }

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
            isShowLoading.observe(viewLifecycleOwner) { isShowLoading ->
                if (isShowLoading) {
                    binding.pbLoading.visibility = View.VISIBLE
                    binding.tvStatementNoDataAvailable.visibility = View.GONE
                } else {
                    binding.pbLoading.visibility = View.GONE
                }
            }

            isShowRefreshModal.observe(viewLifecycleOwner) { isShowingRefreshModal ->
                if (isShowingRefreshModal) {
                    refreshModal.showRefreshModal(
                        ctx,
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

            stories.observe(viewLifecycleOwner) { stories ->
                setStoryList(stories)
            }
        }
    }

    private fun initializeTheStoryListAdapterForTheFirstTime() {
        val adapterStory = ListStoryAdapter(ctx, arrayListOf())
        binding.rvStoryList.adapter = adapterStory
    }

    private fun setStoryList(stories: ArrayList<Story>) {
        val adapterStory = ListStoryAdapter(ctx, stories)
        binding.rvStoryList.adapter = adapterStory

        adapterStory.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
            override fun onClickedItem(data: Story, imageThumbnailsView: View) {
                val moveToStoryDetail = Intent(
                    ctx, StoryDetailActivity::class.java
                )
                moveToStoryDetail.putExtra(StoryDetailActivity.EXTRA_STORY, data)
                startActivity(
                    moveToStoryDetail, ActivityOptionsCompat.makeSceneTransitionAnimation(
                        ctx as Activity, Pair(imageThumbnailsView, "image_detail")
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

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        refreshModal.dismiss()
    }
}