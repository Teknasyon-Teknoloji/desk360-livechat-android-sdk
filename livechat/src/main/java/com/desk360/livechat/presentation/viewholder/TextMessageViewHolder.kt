package com.desk360.livechat.presentation.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.desk360.livechat.data.model.chat.Message
import com.desk360.livechat.databinding.ItemMessageTextBinding
import com.desk360.livechat.presentation.viewmodel.TextMessageViewModel

class TextMessageViewHolder(private val binding: ItemMessageTextBinding) :
    BaseMessageViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup) = TextMessageViewHolder(
            ItemMessageTextBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun bindTo(model: Message, position: Int) {
        super.bindTo(model, position)

        val viewModel = TextMessageViewModel()
        binding.viewModel = viewModel
        viewModel.handleMessage(model)
    }
}