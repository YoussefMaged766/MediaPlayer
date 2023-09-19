package com.example.mediaplayer.ui.video

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediaplayer.models.VideoItem
import com.example.mediaplayer.utils.GalleryUtil
import com.example.mediaplayer.utils.VideoUtil
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
class VideosFragmentViewModel @Inject constructor(
    private val application: Application
) :ViewModel() {
    private val _videos = MutableStateFlow<List<VideoItem>>(emptyList())
    val videos: StateFlow<List<VideoItem>> = _videos


    init {
        getAllVideos()
    }

     fun getAllVideos(){
        viewModelScope.launch (Dispatchers.IO){
            VideoUtil.getVideoThumbnails(application).collect {videosList ->
                _videos.value = videosList
                Log.e("getAllVideos: ", videosList.toString())
            }
        }
    }
}