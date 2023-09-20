package com.example.mediaplayer.ui.image

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.ContentObserver
import android.os.Build
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
import com.example.mediaplayer.ui.adapter.ImagesAdapter
import com.example.mediaplayer.databinding.FragmentImagesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ImagesFragment : Fragment() {

    lateinit var binding: FragmentImagesBinding
    private val viewModel: ImagesFragmentViewModel by viewModels()
    private val adapter: ImagesAdapter by lazy { ImagesAdapter() }
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

   private val contentObserver: ContentObserver by lazy {
        object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                viewModel.getAllPhotos()
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
        binding = FragmentImagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      initRecyclerView()

        initContentObserver()
        checkPermission()
        handelPermission()


    }

    private fun initRecyclerView(){
        val layoutManager = GridLayoutManager(requireContext(), calculateSpanCount())
        binding.recyclerViewImages.layoutManager = layoutManager
    }

    private fun collectStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.photos.collect {
                adapter.submitList(it)

            }
        }
        binding.recyclerViewImages.adapter = adapter
    }
    private fun calculateSpanCount(): Int {
        val orientation = resources.configuration.orientation
        return if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            6
        } else {
            3
        }
    }


    private fun handelPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // The OS version of the device is less than Android 10, open the camera or gallery
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_VIDEO
                )
                == PackageManager.PERMISSION_GRANTED

            ) {
                // Both permissions are already granted, open the camera or gallery
                collectStates()
            } else {
                // One or both permissions are not granted, request them
                val permissions = arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO
                )
                permissionLauncher.launch(permissions)
            }
        } else {
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


    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->

                val isGalleryPermissionGrantedImaged =
                    permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false

                val isGalleryPermissionGrantedVideos =
                    permissions[Manifest.permission.READ_MEDIA_VIDEO] ?: false

                if (isGalleryPermissionGrantedImaged && isGalleryPermissionGrantedVideos) {
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
        } else {
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

    private fun initContentObserver() {

        requireActivity().contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver

        )
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().contentResolver.unregisterContentObserver(contentObserver)
    }


}