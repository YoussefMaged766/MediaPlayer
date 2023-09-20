package com.example.mediaplayer.ui.video

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.ContentObserver
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediaplayer.ui.adapter.VideosAdapter
import com.example.mediaplayer.databinding.FragmentVideosBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VideosFragment : Fragment() {

    lateinit var binding: FragmentVideosBinding
    private val viewModel: VideosFragmentViewModel by viewModels()
    private val adapter: VideosAdapter by lazy { VideosAdapter() }
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    private val contentObserver: ContentObserver by lazy {
        object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                viewModel.getAllVideos()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initContentObserver()
        collectStates()

    }

    private fun collectStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.videos.collect {
                adapter.submitList(it)

            }
        }
        binding.recyclerViewVideos.adapter = adapter
    }

    private fun initRecyclerView() {
        binding.recyclerViewVideos.layoutManager = GridLayoutManager(
            requireContext(),
            calculateSpanCount()
        )
    }

    private fun calculateSpanCount(): Int {
        val orientation = resources.configuration.orientation
        return if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            4
        } else {
            2
        }
    }


    private fun initContentObserver() {

        requireActivity().contentResolver.registerContentObserver(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver

        )
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().contentResolver.unregisterContentObserver(contentObserver)
    }


}