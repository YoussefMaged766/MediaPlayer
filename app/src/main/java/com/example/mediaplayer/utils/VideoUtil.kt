package com.example.mediaplayer.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.mediaplayer.models.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

object VideoUtil {

    fun getVideoThumbnails(context: Context): Flow<List<VideoItem>> = flow {
        val videoList = mutableListOf<VideoItem>()

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE
        )
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use { c ->
            val idColumn = c.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = c.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)

            while (c.moveToNext()) {
                val videoId = c.getLong(idColumn)
                val videoName = c.getString(nameColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    videoId.toString()
                )

                    videoList.add(VideoItem(contentUri.toString()))

            }
        }

        emit(videoList)
    }.flowOn(Dispatchers.IO)




}