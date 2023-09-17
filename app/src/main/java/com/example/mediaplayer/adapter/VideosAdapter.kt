package com.example.mediaplayer.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.example.mediaplayer.databinding.ImageItemBinding
import com.example.mediaplayer.databinding.VideoItemBinding
import com.example.mediaplayer.models.VideoItem
import java.io.File
import com.bumptech.glide.request.transition.Transition
import com.example.mediaplayer.R


class VideosAdapter : ListAdapter<VideoItem, VideosAdapter.viewholder>(PhotoDiffCallback()) {

    class PhotoDiffCallback : DiffUtil.ItemCallback<VideoItem>() {
        override fun areItemsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
            return oldItem.videoPath == newItem.videoPath
        }

        override fun areContentsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
            return oldItem.videoPath == newItem.videoPath
        }
    }

    class viewholder(var binding: VideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: VideoItem) {


            Glide.with(binding.root)
                .asBitmap()
                .load(data.videoPath)
                .listener(object : RequestListener<Bitmap> {


                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.isVisible = false
                        binding.imgPlay.isVisible = true
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {

                        binding.progressBar.isVisible = false
                        binding.imgPlay.isVisible = true
                        return false
                    }


                }).into(binding.imageView)


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val binding = VideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewholder(binding)
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        holder.bind(getItem(position))
    }
}