package com.example.mediaplayer.ui

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.utils.GalleryUtil
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImagesFragmentViewModel @Inject constructor(
    private val application: Application
) :ViewModel() {
    private val _photos = MutableStateFlow<List<Uri>>(emptyList())
    val photos: StateFlow<List<Uri>> = _photos


    init {
        getAllPhotos()
    }

    private fun getAllPhotos(){
        viewModelScope.launch (Dispatchers.IO){
            GalleryUtil.getAllPhotos(application).collect { photosList ->
                _photos.value = photosList
                Log.e("getAllPhotos: ", photosList.toString())
            }
        }
    }
}