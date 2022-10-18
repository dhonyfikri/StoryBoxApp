package com.fikri.submissionstoryappbpai.fragment.home_ui_item.create_story_options

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fikri.submissionstoryappbpai.databinding.FragmentCreateStoryOptionsBinding

class CreateStoryOptionsFragment : Fragment() {

    companion object {
        const val CREATE_BASIC_STORY = "create_basic_story"
        const val CREATE_GEOLOCATION_STORY = "create_geolocation_story"
    }

    private var _binding: FragmentCreateStoryOptionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var ctx: Context
    private lateinit var createStoryOptionsListener: CreateStoryOptionsListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateStoryOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cvCreateStory.setOnClickListener {
            createStoryOptionsListener.onCreateStoryOptionsClicked(CREATE_BASIC_STORY)
        }

        binding.cvCreateGeolocationStory.setOnClickListener {
            createStoryOptionsListener.onCreateStoryOptionsClicked(CREATE_GEOLOCATION_STORY)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        createStoryOptionsListener = ctx as CreateStoryOptionsListener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface CreateStoryOptionsListener{
        fun onCreateStoryOptionsClicked(options: String)
    }
}