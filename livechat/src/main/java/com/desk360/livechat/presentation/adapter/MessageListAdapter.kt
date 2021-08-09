package com.desk360.livechat.presentation.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.desk360.base.util.ChatUtils
import com.desk360.livechat.data.model.chat.Message
import com.desk360.livechat.presentation.viewholder.*

class MessageListAdapter : ListAdapter<Message, BaseMessageViewHolder>(MessageDiffCallback()) {
    
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseMessageViewHolder {
        return when (viewType) {
            ChatUtils.Message.TIME -> TimeMessageViewHolder.create(parent)
            ChatUtils.Message.PHOTO -> PhotoMessageViewHolder.create(parent)
            ChatUtils.Message.VIDEO -> VideoMessageViewHolder.create(parent)
            ChatUtils.Message.DOCUMENT -> DocumentMessageViewHolder.create(parent)
            else -> TextMessageViewHolder.create(parent)
        }
    }

    override fun onBindViewHolder(holder: BaseMessageViewHolder, position: Int) {
        holder.bindTo(getItem(position), position)
    }

    override fun getItemViewType(position: Int) = getItem(position).type

    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id && oldItem.status == newItem.status
        }
    }
}




