package com.desk360.livechat.presentation.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desk360.livechat.data.HeaderChatScreenModel
import com.desk360.livechat.databinding.ItemLiveChatConversationBinding

class ConversationViewHolder(private val binding: ItemLiveChatConversationBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup) = ConversationViewHolder(
            ItemLiveChatConversationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    fun bind(headerChatScreenModel: HeaderChatScreenModel) {
        binding.viewModel = headerChatScreenModel
    }
}