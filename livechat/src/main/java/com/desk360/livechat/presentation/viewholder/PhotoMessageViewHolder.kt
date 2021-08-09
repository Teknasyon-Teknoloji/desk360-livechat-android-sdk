package com.desk360.livechat.presentation.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.desk360.livechat.data.model.chat.Message
import com.desk360.livechat.databinding.ItemMessagePhotoBinding
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.presentation.viewmodel.PhotoMessageViewModel

class PhotoMessageViewHolder(private val binding: ItemMessagePhotoBinding) :
    BaseMediaMessageViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup) = PhotoMessageViewHolder(
            ItemMessagePhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun bindTo(message: Message, position: Int) {
        super.bindTo(message, position)

        val viewModel = PhotoMessageViewModel()
        binding.viewModel = viewModel
        viewModel.handleMessage(message)

        itemView.setOnClickListener {
            binding.imageViewDownload.visibility = View.GONE
            open(message)
        }

        binding.imageViewDownload.setOnClickListener {
            binding.imageViewDownload.visibility = View.GONE
            download(message)
            binding.imageViewPreview.visibility = View.VISIBLE
        }
    }

    override fun getDownloadDescription() = LiveChatHelper.settings?.data?.language?.downloading
}