package com.desk360.livechat.presentation.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.desk360.livechat.R
import com.desk360.livechat.data.model.chat.Message
import com.desk360.livechat.databinding.ItemMessageVideoBinding
import com.desk360.livechat.manager.Desk360LiveChat
import com.desk360.livechat.manager.FileManager
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.presentation.viewmodel.VideoMessageViewModel

class VideoMessageViewHolder(private val binding: ItemMessageVideoBinding) :
    BaseMediaMessageViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup) = VideoMessageViewHolder(
            ItemMessageVideoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun bindTo(message: Message, position: Int) {
        super.bindTo(message, position)

        val viewModel = VideoMessageViewModel()
        binding.viewModel = viewModel
        viewModel.handleMessage(message)
        val fileName = message.attachments?.firstOrNull()?.name

        if (viewModel.isDownload.value == true) {
            binding.imageViewPreview.setBackgroundDrawable(fileName?.let {
                FileManager.getBitmapDrawable(
                    fileName
                )
            })
            binding.progressBar.visibility = View.GONE
        } else {
            Desk360LiveChat.getContext()?.let { context ->
                binding.imageViewPreview.setBackgroundColor(ContextCompat.getColor(context, R.color.secondTextColor))
            }
        }

        binding.imageViewPlayStop.setOnClickListener {
            binding.imageViewDownload.visibility = View.GONE
            open(message)
        }

        binding.imageViewDownload.setOnClickListener {
            binding.imageViewDownload.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            download(message)
            binding.imageViewPreview.setBackgroundDrawable(fileName?.let {
                FileManager.getBitmapDrawable(
                    fileName
                )
            })
            binding.progressBar.visibility = View.GONE
            binding.imageViewVideo.visibility = View.GONE
            binding.imageViewPlayStop.visibility = View.VISIBLE
        }
    }

    override fun getDownloadDescription() = LiveChatHelper.settings?.data?.language?.downloading
}