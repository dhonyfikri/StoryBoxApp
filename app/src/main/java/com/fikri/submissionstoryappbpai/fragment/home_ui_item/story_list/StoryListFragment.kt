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
import com.fikri.submissionstoryappbpai.R
import com.fikri.submissionstoryappbpai.activity.StoryDetailActivity
import com.fikri.submissionstoryappbpai.adapter.ListStoryAdapter
import com.fikri.submissionstoryappbpai.adapter.LoadingStateAdapter
import com.fikri.submissionstoryappbpai.data_model.Story
import com.fikri.submissionstoryappbpai.databinding.FragmentStoryListBinding
import com.fikri.submissionstoryappbpai.other_class.decideOnState
import com.fikri.submissionstoryappbpai.view_model_factory.ViewModelWithInjectionFactory

class StoryListFragment : Fragment() {

    private var _binding: FragmentStoryListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: StoryListViewModel
    private lateinit var ctx: Context
    private lateinit var adapter: ListStoryAdapter

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
        viewModel = ViewModelProvider(
            this,
            ViewModelWithInjectionFactory(ctx)
        )[StoryListViewModel::class.java]

        binding.srlSwipeRefeshStoryList.setColorSchemeColors(
            ContextCompat.getColor(
                ctx,
                R.color.secondary
            )
        )
    }

    private fun setupAction() {
        viewModel.apply {
            binding.apply {

                adapter.addLoadStateListener { combinedLoadStates ->
                    combinedLoadStates.decideOnState(
                        adapter.itemCount,
                        showLoading = { isLoading ->
                            if (isLoading) {
                                setShowOfflineAlert(false)
                                tvErrorMsg.visibility = View.GONE
                                pbLoading.visibility = View.VISIBLE
                            } else {
                                pbLoading.visibility = View.GONE
                            }
                        },
                        adapterAnyLoadingStateLoading = { isLoading ->
                            changeAdapterLoadingState(isLoading)
                            adapterInitialLoading = isLoading
                        },
                        showEmptyState = { isEmptyInAdapter ->
                            storyAdapterIsEmpty = isEmptyInAdapter
                            syncStoryListEmptyState()
                        },
                        showError = {
                            setShowOfflineAlert(true)
                        }
                    )
                }

                cvOfflineAlert.setOnClickListener {
                    if (!adapterInitialLoading) {
                        if (adapter.itemCount > 0) {
                            rvStoryList.scrollToPosition(0)
                        }
                    }
                    adapter.retry()
                }

                srlSwipeRefeshStoryList.setOnRefreshListener {
                    srlSwipeRefeshStoryList.isRefreshing = false
                    adapter.refresh()
                }

                rvStoryList.layoutManager = LinearLayoutManager(ctx)

                getStoryCount().observe(requireActivity()) {
                    storyCountInDatabase = it
                    syncStoryListEmptyState()
                }

                showEmptyStoryMessage.observe(requireActivity()) { isShow ->
                    if (isShow) {
                        tvErrorMsg.text = getString(R.string.empty_story_list)
                        tvErrorMsg.visibility = View.VISIBLE
                    } else {
                        tvErrorMsg.visibility = View.GONE
                    }
                }

                adapterStillLoading.observe(requireActivity()) { isLoading ->
                    srlSwipeRefeshStoryList.isEnabled = !isLoading
                }

                token.observe(requireActivity()) { token ->
                    if (token.isNotEmpty()) {
                        getStory()
                    }
                }
            }
        }
    }

    private fun getStory() {
        binding.rvStoryList.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter(ctx) {
                adapter.retry()
            }
        )
        viewModel.stories.observe(requireActivity()) { data ->
            adapter.submitData(lifecycle, data)
        }

        adapter.setOnItemClickCallback(object : ListStoryAdapter.OnItemClickCallback {
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
    }

    private fun setShowOfflineAlert(isShowing: Boolean = false) {
        if (isShowing) {
            viewModel.offlineAlertAlpha = 1f
            viewModel.offlineAlertTranslationY = 0f
        } else {
            viewModel.offlineAlertAlpha = 0f
            viewModel.offlineAlertTranslationY = -120f
        }
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(
                    binding.cvOfflineAlert,
                    View.TRANSLATION_Y,
                    viewModel.offlineAlertTranslationY
                ).setDuration(500),
                ObjectAnimator.ofFloat(
                    binding.cvOfflineAlert,
                    View.ALPHA,
                    viewModel.offlineAlertAlpha
                ).setDuration(500)
            )
            start()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        adapter = ListStoryAdapter(ctx)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}