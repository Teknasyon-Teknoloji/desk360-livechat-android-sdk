package com.desk360.livechat.presentation.adapter

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desk360.livechat.data.HeaderChatScreenModel
import com.desk360.livechat.presentation.activity.BaseChatActivity
import com.desk360.livechat.presentation.activity.livechat.LiveChatActivity
import com.desk360.livechat.presentation.viewholder.ConversationViewHolder

class ConversationListAdapter(private val context: Context) :
    RecyclerView.Adapter<ConversationViewHolder>() {
    private val conversations = arrayListOf<HeaderChatScreenModel>()

    fun submitList(newList: List<HeaderChatScreenModel>) {
        conversations.clear()
        conversations.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ConversationViewHolder.create(parent)

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversations[position]
        holder.bind(conversation)
        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, LiveChatActivity::class.java).apply {
                putExtra(BaseChatActivity.EXTRA_CONVERSATION_ID, conversation.id)
            })
        }
    }

    override fun getItemCount() = conversations.size
}