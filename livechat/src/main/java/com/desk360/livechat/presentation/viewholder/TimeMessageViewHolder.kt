package com.desk360.livechat.presentation.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.desk360.livechat.data.model.chat.Message
import com.desk360.livechat.databinding.ItemMessageTimeBinding
import com.desk360.livechat.manager.LiveChatHelper

class TimeMessageViewHolder(private val binding: ItemMessageTimeBinding) :
    BaseMessageViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup) = TimeMessageViewHolder(
            ItemMessageTimeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun bindTo(message: Message, position: Int) {
        super.bindTo(message, position)
        binding.time = message.body
        binding.color = LiveChatHelper.settings?.data?.config?.general?.backgroundHeaderColor
    }
}