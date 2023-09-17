package com.example.mediaplayer.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.example.mediaplayer.R
import com.example.mediaplayer.adapter.VideosAdapter
import com.example.mediaplayer.databinding.FragmentVideosBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VideosFragment : Fragment() {

    lateinit var binding: FragmentVideosBinding
    private val viewModel: VideosFragmentViewModel by viewModels()
    private val adapter: VideosAdapter by lazy { VideosAdapter() }
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
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
        checkPermission()
        handelPermission()
    }

    private fun collectStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.videos.collect {
                adapter.submitList(it)

            }
        }
        binding.recyclerViewVideos.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun handelPermission() {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Both permissions are already granted, open the camera or gallery
            collectStates()
        } else {
            // One or both permissions are not granted, request them
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            permissionLauncher.launch(permissions)
        }
    }

    private fun checkPermission() {

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val isGalleryPermissionGranted =
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false

            if (isGalleryPermissionGranted) {
                // Both permissions are granted, do something
                collectStates()
            } else {
                // One or both permissions are not granted, show a message or take some other action
                Toast.makeText(
                    requireContext(),
                    "Camera and gallery permissions are required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


}